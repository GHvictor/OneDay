package com.android.oneday.activity.MainPageActivity;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 3/3/2016.
 * TODO finish 70%.need change img.
 */
public class MainPageActivity extends BaseActivity implements OnPageChangeListener{

    /**
     * Param statement
     */
    private ViewPager pager = null;
    private TextView textTab = null;
    private LocalActivityManager manager = null;
    private ImageView imgCalender, imgSchedule, imgWeather;
    private String[] mlistTag = {"one", "two", "three"}; //activity标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initData(savedInstanceState);
        initView();
        initViewPage();
    }

    @Override
    protected void onDestroy() {
        //super.unregisterReceiver(MainPageActivity.this.receiver);
        super.onDestroy();
    }
    protected void onStart(){
        super.onStart();
    }
    protected void onStop(){
        super.onStop();
    }

    public void finish() {
        this.moveTaskToBack(true);
    }

    private void initData(Bundle savedInstanceState) {
        this.manager = new LocalActivityManager(this, true);
        this.manager.dispatchCreate(savedInstanceState);
    }

    private void initView() {
        this.textTab = (TextView) super.findViewById(R.id.textTopTab);
        this.imgCalender = (ImageView) super.findViewById(R.id.calendarImage);
        this.imgSchedule = (ImageView) super.findViewById(R.id.scheduleImage);
        this.imgWeather = (ImageView) super.findViewById(R.id.weatherImage);
    }

    private void initViewPage() {
        final List<View> listViews = new ArrayList<View>();
        this.pager = (ViewPager) findViewById(R.id.viewpager_main);
        Intent intentCalendar = new Intent(MainPageActivity.this, CalendarPageActivity.class); // 加载activity到viewpage
        View viewCalendar = getView(mlistTag[0], intentCalendar);
        if (viewCalendar != null){
            listViews.add(viewCalendar);
        }
        Intent intentSchedule = new Intent(MainPageActivity.this, SchedulePageActivity.class); // 加载activity到viewpage
        View viewSchedule = getView(mlistTag[1], intentSchedule);
        if (viewSchedule != null){
            listViews.add(viewSchedule);
        }
        Intent intentWeather = new Intent(MainPageActivity.this, WeatherPageActivity.class); // 加载activity到viewpage
        View viewWeather = getView(mlistTag[2], intentWeather);
        if (viewWeather != null){
            listViews.add(viewWeather);
        }
        this.pager.setAdapter(new MainFramePagerAdapter(listViews));
        this.pager.setCurrentItem(1);
        this.pager.addOnPageChangeListener(this);
        this.textTab.setText("日 程");
        //this.text2.setTextColor(getResources().getColor(R.color.text_checked));
        //this.image2.setImageDrawable(getResources().getDrawable(R.drawable.contacts_press));
    }

    public void PageOnClick(View view) {
        switch (view.getId()) {
            case R.id.calendarPage:
                this.pager.setCurrentItem(0);
                this.textTab.setText("日 历");
                this.imgCalender.setImageDrawable(getResources().getDrawable(R.drawable.menu_calendar));
                this.imgSchedule.setImageDrawable(getResources().getDrawable(R.drawable.menu_schedule));
                this.imgWeather.setImageDrawable(getResources().getDrawable(R.drawable.menu_weather));
                break;
            case R.id.schedulePage:
                this.pager.setCurrentItem(1);
                this.textTab.setText("日 程");
                this.imgCalender.setImageDrawable(getResources().getDrawable(R.drawable.menu_calendar));
                this.imgSchedule.setImageDrawable(getResources().getDrawable(R.drawable.menu_schedule));
                this.imgWeather.setImageDrawable(getResources().getDrawable(R.drawable.menu_weather));
                break;
            case R.id.weatherPage:
                this.pager.setCurrentItem(2);
                this.textTab.setText("天 气");
                this.imgCalender.setImageDrawable(getResources().getDrawable(R.drawable.menu_calendar));
                this.imgSchedule.setImageDrawable(getResources().getDrawable(R.drawable.menu_schedule));
                this.imgWeather.setImageDrawable(getResources().getDrawable(R.drawable.menu_weather));
                break;
            default:
                break;
        }
    }

    /**
     * 加载activity
     */
    private View getView(String id, Intent intent) {
        final Window w = manager.startActivity(id, intent);
        final View view = w != null ? w.getDecorView() : null;
        return view;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                this.pager.setCurrentItem(0);
                this.textTab.setText("日 历");
                this.imgCalender.setImageDrawable(getResources().getDrawable(R.drawable.menu_calendar));
                this.imgSchedule.setImageDrawable(getResources().getDrawable(R.drawable.menu_schedule));
                this.imgWeather.setImageDrawable(getResources().getDrawable(R.drawable.menu_weather));
                break;
            case 1:
                this.pager.setCurrentItem(1);
                this.textTab.setText("日 程");
                this.imgCalender.setImageDrawable(getResources().getDrawable(R.drawable.menu_calendar));
                this.imgSchedule.setImageDrawable(getResources().getDrawable(R.drawable.menu_schedule));
                this.imgWeather.setImageDrawable(getResources().getDrawable(R.drawable.menu_weather));
                break;
            case 2:
                this.pager.setCurrentItem(2);
                this.textTab.setText("天 气");
                this.imgCalender.setImageDrawable(getResources().getDrawable(R.drawable.menu_calendar));
                this.imgSchedule.setImageDrawable(getResources().getDrawable(R.drawable.menu_schedule));
                this.imgWeather.setImageDrawable(getResources().getDrawable(R.drawable.menu_weather));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class MainFramePagerAdapter extends PagerAdapter {

        public List<View> mainListViews;

        public MainFramePagerAdapter(List<View> mListViews) {
            this.mainListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mainListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mainListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mainListViews.get(arg1), 0);
            return mainListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }
    }

}
