package com.livechatinc.inappchat;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;

import androidx.annotation.NonNull;

public class ChatWindowUtils {
    /**
     * Creates an instance of ChatWindowView an attaches to the provided activity.
     * ChatWindowView is hidden until it is initialized and shown.
     */
    public static ChatWindowView createAndAttachChatWindowInstance(@NonNull Activity activity) {
        final ViewGroup contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        ChatWindowViewImpl chatWindowView = (ChatWindowViewImpl) LayoutInflater.from(activity).inflate(R.layout.view_chat_window, contentView, false);
        contentView.addView(chatWindowView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        return chatWindowView;
    }

    /**
     * Convenience method for removing the view from activity window
     */
    public static void detachChatWindowInstance(@NonNull Activity activity, @NonNull View view) {
        final ViewGroup contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.removeView(view);
    }

    /**
     * Clears cookies and web storage for effective reload
     */
    public static void clearSession(Context context) {
        WebStorage.getInstance().deleteAllData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
