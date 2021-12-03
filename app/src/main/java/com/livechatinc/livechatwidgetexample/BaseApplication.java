package com.livechatinc.livechatwidgetexample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by szymonjarosz on 24/07/2017.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
