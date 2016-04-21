package com.android.oneday.activity.MainPageActivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.activity.CalendarActivity.CalAddScheduleView;
import com.android.oneday.activity.CalendarActivity.CalendarView;
import com.android.oneday.adapter.SortAdapter;
import com.android.oneday.constant.ScheduleConstant;
import com.android.oneday.db.ScheduleModel;
import com.android.oneday.vo.ScheduleVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Feng on 3/3/2016.
 * TODO Start coding.
 */
public class SchedulePageActivity extends BaseActivity {

    private ScrollView sv = null;
    private LinearLayout layout = null;
    private ScheduleModel model = null;
    private ScheduleVO scheduleVO = null;
    private SortAdapter adapter;
    private ArrayList<ScheduleVO> schList = new ArrayList<ScheduleVO>();
    private TextView Schdate = null;
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
        model = new ScheduleModel(this);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);

        initView();
        initData();
    }

    private void initData() {

        Toast.makeText(this, currentDate.toString(), Toast.LENGTH_LONG).show();
        schList = model.getScheduleByDate(currentDate);
        //schList = model.getAllSchedule();
//        Toast.makeText(this, schList.toString(), Toast.LENGTH_SHORT).show();
        if(schList != null) {
            adapter = new SortAdapter(this, schList);
            schListView.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "hahah", Toast.LENGTH_SHORT).show();
            Log.v("hhahah", "hahhahah");
        }
    }

    private void initView() {
        this.Schdate = (TextView) super.findViewById(R.id.schdate);
        this.schListView = (ListView) super.findViewById(R.id.schListView);
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
        this.Schdate.setText(year_c + "年" + month_c + "月" + day_c + "日");
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
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(1, menu.FIRST, menu.FIRST, "返回日历");
        menu.add(1, menu.FIRST+1, menu.FIRST+1, "添加日程");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case Menu.FIRST:
                Intent intent = new Intent();
                intent.setClass(SchedulePageActivity.this, CalendarView.class);
                startActivity(intent);
                break;
            case Menu.FIRST+1:
                Intent intent1 = new Intent();
                intent1.setClass(SchedulePageActivity.this, CalAddScheduleView.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
