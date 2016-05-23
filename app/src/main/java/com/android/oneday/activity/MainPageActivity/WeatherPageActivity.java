package com.android.oneday.activity.MainPageActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.oneday.Network.WebTools;
import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.activity.SettingActivity.SettingCityActivity;
import com.android.oneday.activity.SettingActivity.SettingPageActivity;
import com.android.oneday.util.SysApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Feng on 3/3/2016.
 * TODO this coding.
 */
public class WeatherPageActivity extends BaseActivity {

    //记录壁纸的文件
    public static final String WALLPAPER_FILE = "wallpaper_file";
    //缓存天气的文件
    public static final String STORE_WEATHER = "store_weather";

    private ImageView appSet = null;
    private ImageView weatherUpdate = null;
    private LinearLayout rootLayout;

    private String url;

    private WeatherBroadcastReceiver receiver = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_page);

        initView();
        //通过检查程序中的缓存文件判断程序是否是第一次运行
        String dirPath = "/data/data/com.android.oneday/shared_prefs/";
        File file = new File(dirPath);
        boolean isFirstRun = false;
        //如果文件不存在说明是第一次动行
        if (!file.exists()) {
            //设置默认的壁纸
            SharedPreferences.Editor editor = getSharedPreferences(WALLPAPER_FILE, MODE_PRIVATE).edit();
            editor.putInt("wellpaper", R.drawable.weather_bg00);
            editor.commit();
            isFirstRun = true;
        } else {
            //设置壁纸为文件中保存的
            SharedPreferences sp = getSharedPreferences(WALLPAPER_FILE, MODE_PRIVATE);
            rootLayout.setBackgroundResource(sp.getInt("wellpaper", R.drawable.weather_bg00));
        }

        //得到保存的城市天气
        SharedPreferences sp = getSharedPreferences(SettingCityActivity.CITY_CODE_FILE, MODE_PRIVATE);
        String cityCode = sp.getString("code", "");
        if (cityCode != null && cityCode.trim().length() != 0) {
            SharedPreferences shared = getSharedPreferences(STORE_WEATHER, MODE_PRIVATE);
            long currentTime = System.currentTimeMillis();
            //得到天气缓冲文件中的有效期
            long vaildTime = shared.getLong("validTime", currentTime);
            //比较天气缓存文件中的有效期，如果超时了，则访问网络更新天气
            if (vaildTime > currentTime)
                setWeatherSituation(shared);
            else
                setWeatherSituation(cityCode);
        } else {
            //跳转到设置城市的Activity
            Intent intent = new Intent(WeatherPageActivity.this, SettingCityActivity.class);
            intent.putExtra("isFirstRun", isFirstRun);
            startActivityForResult(intent, 0);
        }
        chooseSet();
        chooseUpdate();
        weatherBroadcas();
    }

    private void initView(){
        this.rootLayout = (LinearLayout) findViewById(R.id.weatherLayout);
        this.appSet = (ImageView) findViewById(R.id.app_set);
        this.weatherUpdate = (ImageView) findViewById(R.id.weatherUpdate);
    }

    private void chooseSet() {
        appSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(WeatherPageActivity.this, SettingPageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void chooseUpdate(){
        weatherUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(SettingCityActivity.CITY_CODE_FILE, MODE_PRIVATE);
                String cityCode = sp.getString("code", "");
                setWeatherSituation(cityCode);
                Toast.makeText(WeatherPageActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //由城市码设置天气情况,并将得到的信息保存在文件中
    public void setWeatherSituation(String cityCode) {
        url = "https://api.heweather.com/x3/weather?cityid=CN" + cityCode + "&key=8aacaf8a471b4b76a2ad9982c8a50031";
        Log.d("haha", url);
        final MyHandler myHandler = new MyHandler();

        new Thread(new Runnable() {
            public void run() {
                String info = null;
                Message msg = new Message();
                Bundle data = new Bundle();

                info = new WebTools(WeatherPageActivity.this).getWebContent(url);
                data.putString("value", info);
                msg.setData(data);
                myHandler.sendMessage(msg);

            }
        }).start();

    }

    //根据已定的缓存文件来得到天气情况
    public void setWeatherSituation(SharedPreferences shared) {
        String info = null;
        TextView tempText = null;
        ImageView imageView = null;

        //得到城市
        info = shared.getString("city", "");
        tempText = (TextView) findViewById(R.id.cityField);
        tempText.setText(info);
        //得到阳历日期
        info = shared.getString("date_y", "");
        tempText = (TextView) findViewById(R.id.date_y);
        tempText.setText(info);
        //得到温度
        info = shared.getString("temp1", "");
        tempText = (TextView) findViewById(R.id.currentTemp);
        tempText.setText(info);
        //得到天气
        info = shared.getString("weather1", "");
        tempText = (TextView) findViewById(R.id.currentWeather);
        tempText.setText(info);
        //天气图标
        imageView = (ImageView) findViewById(R.id.weather_icon01);
        imageView.setImageResource(shared.getInt("img_title1", 0));
        //得到风向
        info = shared.getString("wind1", "");
        tempText = (TextView) findViewById(R.id.currentWind);
        tempText.setText(info);
        //得到建议
        info = shared.getString("index_d", "");
        tempText = (TextView) findViewById(R.id.index_d);
        tempText.setText(info);
        //得到明天的天气
        info = shared.getString("weather2", "");
        tempText = (TextView) findViewById(R.id.weather02);
        tempText.setText(info);
        //明天的图标
        imageView = (ImageView) findViewById(R.id.weather_icon02);
        imageView.setImageResource(shared.getInt("img_title2", 0));
        //明天的气温
        info = shared.getString("temp2", "");
        tempText = (TextView) findViewById(R.id.temp02);
        tempText.setText(info);
        //明天的风力
        info = shared.getString("wind2", "");
        tempText = (TextView) findViewById(R.id.wind02);
        //后天的天气
        info = shared.getString("weather3", "");
        tempText = (TextView) findViewById(R.id.weather03);
        tempText.setText(info);
        //后天天气图标
        imageView = (ImageView) findViewById(R.id.weather_icon03);
        imageView.setImageResource(shared.getInt("img_title3", 0));
        //后天的气温
        info = shared.getString("temp3", "");
        tempText = (TextView) findViewById(R.id.temp03);
        tempText.setText(info);
        //后天的风力
        info = shared.getString("wind3", "");
        tempText = (TextView) findViewById(R.id.wind03);
        tempText.setText(info);
    }

    //由天气情况得到图片
    public static int getWeatherBitMapResource(String weather) {
        Log.i("weather_info", "=============" + weather + "===============");
        if (weather.equals("晴")) {
            return R.drawable.weathericon_condition_01;
        } else if (weather.equals("多云") || weather.equals("晴间多云")) {
            return R.drawable.weathericon_condition_02;
        } else if (weather.equals("阴")) {
            return R.drawable.weathericon_condition_04;
        } else if (weather.equals("雾")) {
            return R.drawable.weathericon_condition_05;
        } else if (weather.equals("沙尘暴")) {
            return R.drawable.weathericon_condition_06;
        } else if (weather.equals("阵雨")) {
            return R.drawable.weathericon_condition_07;
        } else if (weather.equals("小雨") || weather.equals("小到中雨")) {
            return R.drawable.weathericon_condition_08;
        } else if (weather.equals("大雨")) {
            return R.drawable.weathericon_condition_09;
        } else if (weather.equals("雷阵雨")) {
            return R.drawable.weathericon_condition_10;
        } else if (weather.equals("小雪")) {
            return R.drawable.weathericon_condition_11;
        } else if (weather.equals("大雪")) {
            return R.drawable.weathericon_condition_12;
        } else if (weather.equals("雨夹雪")) {
            return R.drawable.weathericon_condition_13;
        } else {
            return R.drawable.weathericon_condition_17;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String info = bundle.getString("value");
            Log.d("hahaha2", info);
            try {
                //==========================解析JSON得到天气===========================
                JSONObject json = new JSONObject(info);
                JSONObject weatherJson = json.getJSONArray("HeWeather data service 3.0").getJSONObject(0);
                JSONObject todayJson = weatherJson.getJSONArray("daily_forecast").getJSONObject(0);
                JSONObject tomorrowJson = weatherJson.getJSONArray("daily_forecast").getJSONObject(1);
                JSONObject afterTowJson = weatherJson.getJSONArray("daily_forecast").getJSONObject(2);
                JSONObject suggestion = weatherJson.getJSONObject("suggestion");
                TextView tempText = null;
                ImageView imageView = null;
                int weather_icon = 0;

                //建立一个缓存天气的文件
                SharedPreferences.Editor editor = getSharedPreferences(STORE_WEATHER, MODE_PRIVATE).edit();

                //得到城市
                info = weatherJson.getJSONObject("basic").getString("city");
                tempText = (TextView) findViewById(R.id.cityField);
                tempText.setText(info);
                editor.putString("city", info);
                //得到阳历日期
                info = weatherJson.getJSONArray("daily_forecast").getJSONObject(0).getString("date");
                tempText = (TextView) findViewById(R.id.date_y);
                tempText.setText(info);
                editor.putString("date_y", info);
                //得到温度
                info = todayJson.getJSONObject("tmp").getString("min") + "℃~" +
                        todayJson.getJSONObject("tmp").getString("max") + "℃";
                tempText = (TextView) findViewById(R.id.currentTemp);
                tempText.setText(info);
                editor.putString("temp1", info);
                //得到天气
                info = todayJson.getJSONObject("cond").getString("txt_d");
                tempText = (TextView) findViewById(R.id.currentWeather);
                tempText.setText(info);
                editor.putString("weather1", info);
                //天气图标
                imageView = (ImageView) findViewById(R.id.weather_icon01);
                weather_icon = getWeatherBitMapResource(info);
                imageView.setImageResource(weather_icon);
                editor.putInt("img_title1", weather_icon);
                //得到风向
                info = todayJson.getJSONObject("wind").getString("dir");
                tempText = (TextView) findViewById(R.id.currentWind);
                tempText.setText(info);
                editor.putString("wind1", info);
                //得到建议
                info = suggestion.getJSONObject("comf").getString("txt");
                tempText = (TextView) findViewById(R.id.index_d);
                tempText.setText(info);
                editor.putString("index_d", info);
                //得到明天的天气
                info = tomorrowJson.getJSONObject("cond").getString("txt_d");
                tempText = (TextView) findViewById(R.id.weather02);
                tempText.setText(info);
                editor.putString("weather2", info);
                //明天的图标
                imageView = (ImageView) findViewById(R.id.weather_icon02);
                weather_icon = getWeatherBitMapResource(info);
                imageView.setImageResource(weather_icon);
                editor.putInt("img_title2", weather_icon);
                //明天的气温
                info = tomorrowJson.getJSONObject("tmp").getString("min") + "℃~" +
                        tomorrowJson.getJSONObject("tmp").getString("max") + "℃";
                tempText = (TextView) findViewById(R.id.temp02);
                tempText.setText(info);
                editor.putString("temp2", info);
                //明天的风力
                info = tomorrowJson.getJSONObject("wind").getString("dir");
                tempText = (TextView) findViewById(R.id.wind02);
                tempText.setText(info);
                editor.putString("wind2", info);
                //后天的天气
                info = afterTowJson.getJSONObject("cond").getString("txt_d");
                tempText = (TextView) findViewById(R.id.weather03);
                tempText.setText(info);
                editor.putString("weather3", info);
                //后天天气图标
                imageView = (ImageView) findViewById(R.id.weather_icon03);
                weather_icon = getWeatherBitMapResource(info);
                imageView.setImageResource(weather_icon);
                editor.putInt("img_title3", weather_icon);
                //后天的气温
                info = afterTowJson.getJSONObject("tmp").getString("min") + "℃~" +
                        afterTowJson.getJSONObject("tmp").getString("max") + "℃";
                tempText = (TextView) findViewById(R.id.temp03);
                tempText.setText(info);
                editor.putString("temp3", info);
                //后天的风力
                info = afterTowJson.getJSONObject("wind").getString("dir");
                tempText = (TextView) findViewById(R.id.wind03);
                tempText.setText(info);
                editor.putString("wind3", info);
                //设置一个有效日期为5小时
                long validTime = System.currentTimeMillis();
                validTime = validTime + 5 * 60 * 60 * 1000;
                editor.putLong("validTime", validTime);

                //保存
                editor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void weatherBroadcas() {
        //动态方式注册广播接收者
        this.receiver = new WeatherBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.oneday.activity.SettingCityActivity");
        this.registerReceiver(receiver, filter);
    }

    public class WeatherBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.android.oneday.activity.SettingCityActivity")) {
                if (intent.getBooleanExtra("updateWeather", true)) {
                    //得到城市的编码
                    SharedPreferences sp = getSharedPreferences(SettingCityActivity.CITY_CODE_FILE, MODE_PRIVATE);
                    String cityCode = sp.getString("code", "");
                    if (cityCode != null && cityCode.trim().length() != 0) {
                        if (intent != null && intent.getBooleanExtra("updateWeather", false)) {
                            //从网上更新新的天气
                            setWeatherSituation(cityCode);
                        } else {
                            //读取缓存文件中的天气
                            SharedPreferences shared = getSharedPreferences(STORE_WEATHER, MODE_PRIVATE);
                            setWeatherSituation(shared);
                        }
                    } else {
                        //如果是没有城市码的回退，则退出程序
                        SysApp.getInstance().exit();
                    }
                }
            } else {
                Toast.makeText(context, "接收失败？！", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
