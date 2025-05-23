package com.livechatinc.livechatwidgetexample;

import android.app.Application;

import com.livechatinc.chatwidget.LiveChat;
import com.livechatinc.chatwidget.src.common.Logger;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        String licence = BuildConfig.LICENCE == null ? "1520" : BuildConfig.LICENCE;

        // To get HTTP calls logs, logger lever must be set before LiveChat.initialize
        Logger.setLogLevel(Logger.LogLevel.ERROR);
        LiveChat.initialize(licence, this);
    }
}
