package com.livechatinc.livechatwidgetexample;

import android.app.Application;

import com.livechatinc.inappchat.ChatWindowConfiguration;
import com.squareup.leakcanary.LeakCanary;

import static com.livechatinc.livechatwidgetexample.MainActivity.LIVECHAT_SUPPORT_LICENCE_NR;

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

    public static ChatWindowConfiguration getChatWindowConfiguration() {
        return new ChatWindowConfiguration(LIVECHAT_SUPPORT_LICENCE_NR, null, "Earl", null, null);
    }
}
