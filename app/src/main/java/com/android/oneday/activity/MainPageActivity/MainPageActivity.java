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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 3/3/2016.
 * TODO finish 30%.
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
        initView();
        //TODO this need when code Calendar finished then test it.
        initViewPage();
    }

    private void initView() {
        this.textTab = (TextView) super.findViewById(R.id.textTopTab);
        this.imgCalender = (ImageView) super.findViewById(R.id.calendarImage);
        this.imgSchedule = (ImageView) super.findViewById(R.id.scheduleImage);
        this.imgWeather = (ImageView) super.findViewById(R.id.weatherImage);
    }

    private void initViewPage() {
        final List<View> mListViews = new ArrayList<View>();
        this.pager = (ViewPager) findViewById(R.id.viewpager_main);
        Intent intent1 = new Intent(MainPageActivity.this, CalendarPageActivity.class); // 加载activity到viewpage
        View v1 = getView(mlistTag[0], intent1);
        mListViews.add(v1);
        Intent intent2 = new Intent(MainPageActivity.this, SchedulePageActivity.class); // 加载activity到viewpage
        mListViews.add(getView("B", intent2));
        Intent intent3 = new Intent(MainPageActivity.this, WeatherPageActivity.class); // 加载activity到viewpage
        mListViews.add(getView("C", intent3));
        this.pager.setAdapter(new MyFramePagerAdapter(mListViews));
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

    private void initData(Bundle savedInstanceState) {
        //TODO db Wait Coding.
    }

    /**
     * 加载activity
     */
    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
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

    public class MyFramePagerAdapter extends PagerAdapter {

        public List<View> mListViews;

        public MyFramePagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
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
