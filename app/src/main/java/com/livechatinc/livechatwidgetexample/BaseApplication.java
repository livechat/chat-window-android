package com.livechatinc.livechatwidgetexample;

import android.app.Application;

import com.livechatinc.chatwidget.LiveChat;
import com.livechatinc.chatwidget.src.common.Logger;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        String license = BuildConfig.LICENSE == null ? "1520" : BuildConfig.LICENSE;

        // To get HTTP calls logs, logger lever must be set before LiveChat.initialize
        Logger.setLogLevel(Logger.LogLevel.VERBOSE);
        LiveChat.initialize(license, this);
    }
}
