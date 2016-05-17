package com.android.oneday.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.android.oneday.R;
import com.android.oneday.activity.Base.BaseActivity;
import com.android.oneday.activity.MainPageActivity.MainPageActivity;
import com.android.oneday.util.SysApp;

/**
 * Created by Feng on 3/3/2016.
 */
public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SysApp.getInstance().addActivity(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainPageActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();//结束本Activity
            }
        }, 3000);
    }
}
