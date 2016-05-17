package com.android.oneday.activity.MainPageActivity;

import android.os.Bundle;

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.util.SysApp;

/**
 * Created by Feng on 3/3/2016.
 * TODO this coding.
 */
public class WeatherPageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_page);
        SysApp.getInstance().addActivity(this);
    }
}
