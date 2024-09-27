package com.livechatinc.inappchat;

import android.net.Uri;

interface ChatWindowViewInternal {
    void loadUrl(String url);

    void showWebView();

    void showProgress();

    void hideProgressBar();

    void hideChatWindow();

    void showErrorView();

    boolean isShown();

    void launchExternalBrowser(Uri uri);

    void runOnMainThread(Runnable runnable);

    void showAttachmentsNotSupportedMessage();
}
