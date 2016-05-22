package com.android.oneday.activity.Base;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by dell on 5/17/2016.
 */
public class MyApplication extends Application{

    private static MyApplication instance;

    public boolean isLocked = false;
    public int lockType = 0;

    LockScreenReceiver receiver ;
    IntentFilter filter ;

    // 单例模式中获取唯一的MyApplication实例
    public static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new LockScreenReceiver();
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(receiver, filter);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.unregisterReceiver(receiver);
    }


    class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
			/* 在这里处理广播 */
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                isLocked  = true;
            }
        }
    }

}
