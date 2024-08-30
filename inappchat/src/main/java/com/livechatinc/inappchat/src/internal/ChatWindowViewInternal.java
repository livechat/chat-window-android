package com.livechatinc.inappchat.src.internal;

public interface ChatWindowViewInternal {
    void loadUrl(String url);

    void showProgress();

    void hideProgressBar();

    void hideChatWindow();

    void showErrorView();

    boolean isShown();

    void runOnMainThread(Runnable runnable);
}
