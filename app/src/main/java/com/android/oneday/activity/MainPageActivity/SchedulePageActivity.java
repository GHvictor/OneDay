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

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.activity.CalendarActivity.CalAddScheduleView;
import com.android.oneday.activity.CalendarActivity.CalendarView;
import com.android.oneday.adapter.SortAdapter;
import com.android.oneday.constant.ScheduleConstant;
import com.android.oneday.db.ScheduleModel;
import com.android.oneday.vo.ScheduleVO;

import java.util.ArrayList;

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
    private String scheduleInfo = "";
    private ListView schListView = null;
    private final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

    private int scheduleID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        model = new ScheduleModel(this);

        initView();
        initData();
    }

    private void initData() {
        schList = model.getAllSchedule();
        if(!schList.isEmpty()) {
            adapter = new SortAdapter(this, schList);
            schListView.setAdapter(adapter);
        }
        else {
            Log.v("hhahah", "hahhahah");
        }
    }

    private void initView() {

        this.schListView = (ListView) super.findViewById(R.id.schListView);
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
