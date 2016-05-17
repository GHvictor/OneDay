package com.android.oneday.util;

import com.android.oneday.activity.Base.BaseActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dell on 5/17/2016.
 */
public class SysApp extends BaseActivity {
    private List<BaseActivity> activityList = new LinkedList<BaseActivity>();
    private static SysApp instance;

    private SysApp() {
    }
    public synchronized static SysApp getInstance() {
        if (null == instance) {
            instance = new SysApp();
        }
        return instance;
    }
    // add Activity
    public void addActivity(BaseActivity activity) {
        activityList.add(activity);
    }

    public void exit() {
        try {
            for (BaseActivity activity : activityList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
