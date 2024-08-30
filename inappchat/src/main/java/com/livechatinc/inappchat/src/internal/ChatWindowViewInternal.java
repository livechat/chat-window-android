package com.livechatinc.inappchat.src.internal;

import android.net.Uri;

public interface ChatWindowViewInternal {
    void loadUrl(String url);

    void showWebView();

    void showProgress();

    void hideProgressBar();

    void hideChatWindow();

    void showErrorView();

    boolean isShown();

    void launchExternalBrowser(Uri uri);

    void runOnMainThread(Runnable runnable);
}
