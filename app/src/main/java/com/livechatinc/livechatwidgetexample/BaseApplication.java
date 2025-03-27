package com.livechatinc.livechatwidgetexample;

import android.app.Application;

import com.livechatinc.chatwidget.LiveChat;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        String licence = BuildConfig.LICENCE == null ? "1520" : BuildConfig.LICENCE;

        LiveChat.initialize(licence, this);
    }
}
