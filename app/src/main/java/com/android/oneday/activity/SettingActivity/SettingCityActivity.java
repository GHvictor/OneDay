package com.android.oneday.activity.SettingActivity;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.oneday.Network.WebTools;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.R;
import com.android.oneday.activity.MainPageActivity.MainPageActivity;
import com.android.oneday.adapter.CityAdapter;
import com.android.oneday.adapter.GPSAdapter;
import com.android.oneday.db.CityModel;
import com.android.oneday.util.LocationXMLParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SettingCityActivity extends BaseActivity {

    private ListView gpsView;
    private ExpandableListView provinceList;
    //定义的用于过滤的文本输入框
    private TextView filterText;

    //定义的一个记录城市码的SharedPreferences文件名
    public static final String CITY_CODE_FILE = ""; //city_code

    private String[][] cityCodes;
    private String[] groups;
    private String[][] childs;

    private CityAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_city);

        initView();
        chooseGPS();
        //当单击自动定位时
        filterListener();
        getInfo();
        initData();

    }

    private void initView() {
        this.gpsView = (ListView) findViewById(R.id.gps_view);
        this.provinceList = (ExpandableListView) findViewById(R.id.provinceList);
        this.filterText = (TextView) findViewById(R.id.filterField);

        //设置自动定位的适配器
        gpsView.setAdapter(new GPSAdapter(SettingCityActivity.this));


    }

    private void chooseGPS() {
        gpsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView localeCity = (TextView) view.findViewById(R.id.locateCityText);
                localeCity.setText("正在定位...");

                final LocateHandler handler = new LocateHandler(localeCity);
                //添加一个线程来处理定位
                new Thread() {
                    public void run() {
                        Map<Integer, String> cityMap = getLocationCityInfo();
                        //记录匹配的城市的索引
                        int provinceIndex = -1;
                        int cityIndex = -1;
                        //传给处理类的数据封装对象
                        Bundle bundle = new Bundle();
                        if (cityMap != null) {
                            //得到图家名
                            String country = cityMap.get(LocationXMLParser.COUNTRYNAME);
                            //只匹配中国地区的天气
                            if (country != null && country.equals("中国")) {
                                //得到省
                                String province = cityMap.get(LocationXMLParser.ADMINISTRATIVEAREANAME);
                                //得到市
                                String city = cityMap.get(LocationXMLParser.LOCALITYNAME);
                                //得到区县
                                String towns = cityMap.get(LocationXMLParser.DEPENDENTLOCALITYNAME);

                                Log.i("GPS", "============" + province + "." + city + "." + towns + "==============");
                                //将GPS定位的城市与提供能查天气的城市进行匹配
                                StringBuilder matchCity = new StringBuilder(city);
                                matchCity.append(".");
                                matchCity.append(towns);
                                //找到省份
                                for (int i = 0; i < groups.length; i++) {
                                    if (groups[i].equals(province)) {
                                        provinceIndex = i;
                                        break;
                                    }
                                }
                                //先从区县开始查找匹配的地区
                                for (int j = 0; j < childs[provinceIndex].length; j++) {
                                    if (childs[provinceIndex][j].equals(matchCity.toString())) {
                                        cityIndex = j;
                                        break;
                                    }
                                }
                                //如果未匹配成功,则换为从城市中查找
                                if (cityIndex == -1) {
                                    for (int j = 0; j < childs[provinceIndex].length; j++) {
                                        if (childs[provinceIndex][j].equals(city)) {
                                            cityIndex = j;
                                            //匹配成功，则退出循环
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        //将其用bundle封装，用于传给Handler
                        bundle.putInt("provinceIndex", provinceIndex);
                        bundle.putInt("cityIndex", cityIndex);

                        Message msg = new Message();
                        msg.setData(bundle);
                        //正式交由handler处理
                        handler.sendMessage(msg);
                    }
                }.start();
            }

        });
    }

    private void filterListener() {
        filterText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                CharSequence filterContent = filterText.getText();
                //设置列表数据过滤结果显示
                adapter.getFilter().filter(filterContent);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
    }

    //将res/raw中的城市数据库导入到安装的程序中的database目录下
    public void importInitDatabase() {
        //数据库的目录
        String dirPath = "/data/data/com.android.oneday/databases";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        //数据库文件
        File dbfile = new File(dir, "oneday.db");
        try {
            if (!dbfile.exists()) {
                dbfile.createNewFile();
            }
            //加载欲导入的数据库
            InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.oneday);
            FileOutputStream fos = new FileOutputStream(dbfile);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getInfo() {
        //得到WeatherPageActivity传过来的intent
        Intent intent = getIntent();
        //通过判断MainActivity传过来的isFirstRun来确定是否为第一次运行
        boolean isFirstRun = intent.getBooleanExtra("isFirstRun", false);
        SharedPreferences sp = getSharedPreferences(CITY_CODE_FILE, MODE_PRIVATE);
        if (sp.getString("code", null) == null) {
            //如果不存在城市码，则说明为第一次运行
            isFirstRun = true;
        }

        //如果为true说明是第一次运行
        if (isFirstRun) {
            //导入城市编码数据库
            importInitDatabase();

            //显示一个对话框说明为第一次运行
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("由于本应用是第一次运行，请选择您需要了解天气的城市").setPositiveButton("确定", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void initData() {
        //增强用户体验，在加载城市列表时显示进度对话框
        final ProgressDialog dialog = getProgressDialog("", "正在加载城市列表...");
        dialog.show();
        //伸缩性列表的加载处理类
        final MyHandler mHandler = new MyHandler();
        new Thread(new Runnable() {
            public void run() {
                //查询处理数据库,装载伸展列表
                CityModel dbHelper = new CityModel(SettingCityActivity.this);
                groups = dbHelper.getAllProvinces();
                List<String[][]> result = dbHelper.getAllCityAndCode(groups);
                childs = result.get(0);
                cityCodes = result.get(1);
                //交给Handler对象加载列表
                Message msg = new Message();
                mHandler.sendMessage(msg);
                dialog.cancel();
                dialog.dismiss();
            }
        }).start();
    }

    //得到一个进度对话框
    public ProgressDialog getProgressDialog(String title, String content) {
        //实例化进度条对话框ProgressDialog
        ProgressDialog dialog = new ProgressDialog(this);

        //可以不显示标题
        dialog.setTitle(title);
        dialog.setIndeterminate(true);
        dialog.setMessage(content);
        dialog.setCancelable(true);
        return dialog;
    }

    //利用GPS功能得到当前位置的城市名
    public synchronized Map<Integer, String> getLocationCityInfo() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //设置一个Criteria标准用于过滤LocationProvider
        Criteria criteria = new Criteria();
        //设置不需要高度信息
        criteria.setAltitudeRequired(false);
        //设置不需要方位信息
        criteria.setBearingRequired(false);
        //设置得到的为免费
        //criteria.setCostAllowed(false);

        //得到最好的可用的Provider
        String provider = locationManager.getBestProvider(criteria, true);
        //得到当前的位置对象
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            double latitude = location.getLatitude();  //得到经度
            double longitude = location.getLongitude(); //得到纬度
            //根据经纬度得到详细的地址信息
            //定义的一个网络访问工具类
            WebTools webTools = new WebTools(this);

            String addressContext = webTools.getWebContent("http://api.map.baidu.com/geocoder?output=xml&location=" +
                    latitude + "," + longitude + "&key=UREA8tsVPjAyR1QWCFvDWy04TSYVwVrb");
            //解析地址信息
            SAXParserFactory spf = SAXParserFactory.newInstance();
            try {
                SAXParser parser = spf.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                LocationXMLParser handler = new LocationXMLParser();
                reader.setContentHandler(handler);

                StringReader read = new StringReader(addressContext);
                // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
                InputSource source = new InputSource(read);

                //开始解析
                reader.parse(source);
                //判断是否存在地址
                if (handler.hasAddress())
                    return handler.getDetailAddress();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    //用于处理装载伸缩性列表的处理类
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //在伸缩性的列表中显示数据库中的省份与城市
            adapter = new CityAdapter(SettingCityActivity.this, provinceList, groups, childs);
            provinceList.setAdapter(adapter);

            //为其子列表选项添加单击事件
            provinceList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    //自动跳至天气的显示界面MainActivity

                    //========得到单击的城市码=======
                    //得到城市名
                    String cityName = (String) adapter.getChild(groupPosition, childPosition);
                    //从数据库中得到城市码
                    CityModel dbHelper = new CityModel(SettingCityActivity.this);
                    String cityCode = dbHelper.getCityCodeByName(cityName);

                    Dialog dialog = getProgressDialog("", "正在加载天气...");
                    dialog.show();
                    GoToMainActivity thread = new GoToMainActivity(cityCode, dialog);
                    thread.start();

                    return false;
                }

            });
        }
    }

    //用于处理用户的定位信息
    private class LocateHandler extends Handler {
        //记录定位的文本视图组件
        private TextView textView;

        public LocateHandler(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            int provinceIndex = data.getInt("provinceIndex");
            int cityIndex = data.getInt("cityIndex");
            //判断定位匹配是否成功
            if (provinceIndex >= 0 && provinceIndex < groups.length &&
                    cityIndex >= 0 && cityIndex < childs[provinceIndex].length) {
                //显示定位的城市
                textView.setText(childs[provinceIndex][cityIndex]);

                //自动跳至天气的显示界面MainActivity
                Dialog dialog = getProgressDialog("", "正在加载天气...");
                dialog.show();
                GoToMainActivity thread = new GoToMainActivity(cityCodes[provinceIndex][cityIndex], dialog);
                thread.start();
            } else {
                textView.setText("定位失败！");
            }
        }
    }

    //处理用户选择好城市后的跳转到MainActivity
    private class GoToMainActivity extends Thread {

        //保证跳转的城市码
        private String cityCode;
        //跳转后显示的进度对话框
        private Dialog dialog;

        public GoToMainActivity(String cityCode, Dialog dialog) {
            this.cityCode = cityCode;
            this.dialog = dialog;
        }

        public void run() {
            //得到一个私有的SharedPreferences文件编辑对象
            SharedPreferences.Editor edit = getSharedPreferences(CITY_CODE_FILE, MODE_PRIVATE).edit();
            //将城市码保存
            edit.putString("code", cityCode);
            edit.commit();

            Intent intent = new Intent();
            intent.setAction("com.android.oneday.activity.SettingCityActivity");        //设置Action
            intent.putExtra("updateWeather", true);    //添加附加信息
            sendBroadcast(intent);

            finish();
            dialog.cancel();
            dialog.dismiss();
        }
    }

}
