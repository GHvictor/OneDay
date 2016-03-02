package com.android.oneday.activity.Base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

/**
 * Created by Feng on 3/3/2016.
 */
public abstract class BaseActivity extends Activity{
    private static final String TAG = "tag_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, this.getClass().getSimpleName() + "--->onCreate");
        //设置所以的活动都是无标题的
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //将活动添加到全局变量中 MyApplication
        //MyApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, this.getClass().getSimpleName() + "--->onDestroy");
    }
}
