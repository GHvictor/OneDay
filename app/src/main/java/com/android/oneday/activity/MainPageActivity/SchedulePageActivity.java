package com.android.oneday.activity.MainPageActivity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.activity.CalendarActivity.CalAddScheduleView;
import com.android.oneday.activity.SettingActivity.SettingPageActivity;
import com.android.oneday.adapter.SortAdapter;
import com.android.oneday.constant.ScheduleConstant;
import com.android.oneday.db.ScheduleModel;
import com.android.oneday.util.SysApp;
import com.android.oneday.vo.ScheduleVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Feng on 3/3/2016.
 * TODO Start coding.
 */
public class SchedulePageActivity extends BaseActivity implements OnItemLongClickListener{

    private ScrollView sv = null;
    private LinearLayout layout = null;
    private ScheduleModel model = null;
    private ScheduleVO scheduleVO = null;
    private SortAdapter adapter;
    private ArrayList<ScheduleVO> schList = new ArrayList<ScheduleVO>();
    private TextView schDate = null;
    private ImageView appSet = null;
    private ImageView gotoSch = null;
    private String scheduleInfo = "";
    private String currentDate = "";
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private ListView schListView = null;

    private int scheduleID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_schedule_page);
        SysApp.getInstance().addActivity(this);
        model = new ScheduleModel(this);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);

        initView();
        initData();
        chooseSet();
        chooseSch();
    }

    private void initData() {

        Toast.makeText(this, currentDate.toString(), Toast.LENGTH_LONG).show();
        schList = model.getScheduleByDate(currentDate);

        if(schList != null) {
            adapter = new SortAdapter(this, schList);
            schListView.setAdapter(adapter);
        }
        else {
            schList = new ArrayList<ScheduleVO>();
            adapter = new SortAdapter(this, schList);
            adapter.notifyDataSetChanged();
            schListView.setAdapter(adapter);
            Toast.makeText(this, "还没有记录啊", Toast.LENGTH_SHORT).show();
            Log.v("hhahah", "hahhahah");
        }
    }

    private void initView() {
        this.schDate = (TextView) super.findViewById(R.id.schdate);
        this.schListView = (ListView) super.findViewById(R.id.schListView);
        this.schListView.setOnItemLongClickListener(this);
        this.appSet = (ImageView) findViewById(R.id.app_set);
        this.gotoSch = (ImageView) findViewById(R.id.calSchGotoButton);
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
        this.schDate.setText(year_c + "年" + month_c + "月" + day_c + "日");
    }

    private void chooseSch() {
        gotoSch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(SchedulePageActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        //1901-1-1 ----> 2049-12-31
                        if (year < 1901 || year > 2049) {
                            //不在查询范围内
                            new AlertDialog.Builder(SchedulePageActivity.this).setTitle("错误日期").setMessage("跳转日期范围(1901/1/1-2049/12/31)").setPositiveButton("确认", null).show();
                        } else {

                            //跳转之后将跳转之后的日期设置为当期日期
                            year_c = year;
                            month_c = monthOfYear + 1;
                            day_c = dayOfMonth;
                            currentDate = Integer.toString(year_c) + "-" + Integer.toString(month_c) + "-" + Integer.toString(day_c);
                            initData();
                            schDate.setText(year_c + "年" + month_c + "月" + day_c + "日");
                        }
                    }
                }, year_c, month_c - 1, day_c).show();
            }
        });
    }

    private void chooseSet(){
        appSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SchedulePageActivity.this, SettingPageActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 得到所有的日程信息
     */
    public void getScheduleAll(){
        schList = model.getAllSchedule();
        if(schList != null){
            for (ScheduleVO vo : schList) {
                String content = vo.getScheduleContent();
                int startLine = content.indexOf("\n");
                if(startLine > 0){
                    content = content.substring(0, startLine)+"...";
                }else if(content.length() > 30){
                    content = content.substring(0, 30)+"...";
                }
                scheduleInfo = ScheduleConstant.sch_type[vo.getScheduleTypeID()]+"\n"+vo.getScheduleDate()+"\n"+content;
                scheduleID = vo.getScheduleID();
            }
        }else{
            scheduleInfo = "没有日程";
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("CallLogActivity", view.toString() + "position=" + position);
        final int scheduleID = schList.get(position).getScheduleID();
        String dd = schList.get(position).getScheduleContent();
        new AlertDialog.Builder(this).setTitle("更改日程")
                .setMessage("是否对日程进行编辑")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(SchedulePageActivity.this, CalAddScheduleView.class);
                        //intent.putStringArrayListExtra("scheduleDate", currentDate);
                        intent.putExtra("scheduleID", scheduleID);
                        startActivity(intent);
                        finish();
                        //model.delete(scheduleID);
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("hahah","ok");
                    }
                }).show();
        return true;
    }
}
