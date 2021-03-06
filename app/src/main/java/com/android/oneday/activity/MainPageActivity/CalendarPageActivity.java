package com.android.oneday.activity.MainPageActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.activity.CalendarActivity.CalAddScheduleView;
import com.android.oneday.activity.CalendarActivity.CalendarView;
import com.android.oneday.activity.SettingActivity.SettingPageActivity;
import com.android.oneday.util.SysApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Feng on 3/3/2016.
 */
public class CalendarPageActivity extends BaseActivity implements OnGestureListener {

    private ViewFlipper flipper = null;
    private GestureDetector gestureDetector = null;
    private CalendarView calView = null;
    private GridView gridView = null;
    private Drawable draw = null;
    private ImageView appSet = null;
    private ImageView imageView = null;

    private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）
    private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private String currentDate = "";

    public CalendarPageActivity() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        currentDate = dateFormat.format(date);
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_page);
        initView();
        addGridView();
        gridView.setAdapter(calView);
        flipper.addView(gridView, 0);
        chooseSet();
        chooseCal();

    }

    private void initView(){
        gestureDetector = new GestureDetector(this);
        flipper = (ViewFlipper) findViewById(R.id.calFlipper);
        flipper.removeAllViews();
        appSet = (ImageView) findViewById(R.id.app_set);
        imageView = (ImageView) findViewById(R.id.calGotoButton);
        calView = new CalendarView(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
    }

    private void chooseSet(){
        appSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CalendarPageActivity.this, SettingPageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void chooseCal(){
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(CalendarPageActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        //1901-1-1 ----> 2049-12-31
                        if(year < 1901 || year > 2049){
                            //不在查询范围内
                            new AlertDialog.Builder(CalendarPageActivity.this).setTitle("错误日期").setMessage("跳转日期范围(1901/1/1-2049/12/31)").setPositiveButton("确认", null).show();
                        }else{
                            int gvFlag = 0;
                            addGridView();   //添加一个gridView
                            calView = new CalendarView(CalendarPageActivity.this, CalendarPageActivity.this.getResources(), year, monthOfYear+1, dayOfMonth);
                            gridView.setAdapter(calView);
                            gvFlag++;
                            flipper.addView(gridView,gvFlag);
                            if(year == year_c && monthOfYear+1 == month_c){
                                //nothing to do
                                Log.i("hahaha", "hahaha");
                            }
                            if((year == year_c && monthOfYear+1 > month_c) || year > year_c ){
                                CalendarPageActivity.this.flipper.setInAnimation(AnimationUtils.loadAnimation(CalendarPageActivity.this,R.anim.push_up_in));
                                CalendarPageActivity.this.flipper.setOutAnimation(AnimationUtils.loadAnimation(CalendarPageActivity.this,R.anim.push_up_out));
                                CalendarPageActivity.this.flipper.showNext();
                            }else{
                                CalendarPageActivity.this.flipper.setInAnimation(AnimationUtils.loadAnimation(CalendarPageActivity.this,R.anim.push_down_in));
                                CalendarPageActivity.this.flipper.setOutAnimation(AnimationUtils.loadAnimation(CalendarPageActivity.this,R.anim.push_down_out));
                                CalendarPageActivity.this.flipper.showPrevious();
                            }
                            flipper.removeViewAt(0);
                            //跳转之后将跳转之后的日期设置为当期日期
                            year_c = year;
                            month_c = monthOfYear+1;
                            day_c = dayOfMonth;
                            jumpMonth = 0;
                            jumpYear = 0;
                        }
                    }
                },year_c, month_c-1, day_c).show();
            }
        });
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int gvFlag = 0;         //每次添加gridview到viewflipper中时给的标记
        if (e1.getY() - e2.getY() > 120) {
            //像下滑动
            addGridView();   //添加一个gridView
            jumpMonth++;     //下一个月

            calView = new CalendarView(this, getResources(), jumpMonth, jumpYear,
                    year_c, month_c, day_c);
            gridView.setAdapter(calView);
            gvFlag++;
            flipper.addView(gridView, gvFlag);
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_down_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_down_out));
            this.flipper.showNext();
            flipper.removeViewAt(0);
            return true;
        } else if (e1.getY() - e2.getY() < -120) {
            //向上滑动
            addGridView();   //添加一个gridView
            jumpMonth--;     //上一个月

            calView = new CalendarView(this, getResources(), jumpMonth, jumpYear,
                    year_c, month_c, day_c);
            gridView.setAdapter(calView);
            gvFlag++;
            flipper.addView(gridView,gvFlag);

            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_up_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_up_out));
            this.flipper.showPrevious();
            flipper.removeViewAt(0);
            return true;
        }
        return false;
    }

    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //取得屏幕的宽度和高度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth();
        int Height = display.getHeight();

        gridView = new GridView(this);
        gridView.setNumColumns(7);
        gridView.setColumnWidth(46);

        if(Width == 480 && Height == 800){
            gridView.setColumnWidth(69);
        }
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setBackgroundResource(R.drawable.cal_gridview_bk);
        gridView.setOnTouchListener(new View.OnTouchListener() {
            //将gridview中的触摸事件回传给gestureDetector
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return CalendarPageActivity.this.gestureDetector
                        .onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //gridView中的每一个item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                int startPosition = calView.getStartPositon();
                int endPosition = calView.getEndPosition();
                if (startPosition <= position && position <= endPosition) {
                    String scheduleDay = calView.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
                    String scheduleYear = calView.getShowYear();
                    String scheduleMonth = calView.getShowMonth();
                    String week = "";

                    //得到这一天是星期几
                    switch (position % 7) {
                        case 0:
                            week = "星期日";
                            break;
                        case 1:
                            week = "星期一";
                            break;
                        case 2:
                            week = "星期二";
                            break;
                        case 3:
                            week = "星期三";
                            break;
                        case 4:
                            week = "星期四";
                            break;
                        case 5:
                            week = "星期五";
                            break;
                        case 6:
                            week = "星期六";
                            break;
                    }

                    ArrayList<String> scheduleDate = new ArrayList<String>();
                    scheduleDate.add(scheduleYear);
                    scheduleDate.add(scheduleMonth);
                    scheduleDate.add(scheduleDay);
                    scheduleDate.add(week);

                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("scheduleDate", scheduleDate);
                    intent.setClass(CalendarPageActivity.this, CalAddScheduleView.class);
                    startActivity(intent);
                }
            }
        });
        gridView.setLayoutParams(params);
    }

    /**
     *  TODO 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, menu.FIRST, menu.FIRST, "今天");
        menu.add(0, menu.FIRST + 1, menu.FIRST + 1, "跳转");
        menu.add(0, menu.FIRST + 2, menu.FIRST + 2, "日程");
        menu.add(0, menu.FIRST + 3, menu.FIRST + 3, "日期转换");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    // TODO 头部的年份闰哪月等信息
    public void addTextToTopTextView(TextView view){
        StringBuffer textDate = new StringBuffer();
        draw = getResources().getDrawable(R.drawable.cal_top_day);
        view.setBackgroundDrawable(draw);
        textDate.append(calView.getShowYear()).append("年").append(
                calView.getShowMonth()).append("月").append("\t");
        if (!calView.getLeapMonth().equals("") && calView.getLeapMonth() != null) {
            textDate.append("闰").append(calView.getLeapMonth()).append("月")
                    .append("\t");
        }
        textDate.append(calView.getAnimalsYear()).append("年").append("(").append(
                calView.getCyclical()).append("年)");
        view.setText(textDate);
        view.setTextColor(Color.BLACK);
        view.setTypeface(Typeface.DEFAULT_BOLD);
    }

}
