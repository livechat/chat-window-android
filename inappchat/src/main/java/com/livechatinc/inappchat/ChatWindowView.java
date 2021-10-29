package com.livechatinc.inappchat;

import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * Created by szymonjarosz on 20/07/2017.
 */

public interface ChatWindowView {


    boolean setConfiguration(@NonNull ChatWindowConfiguration config); //set config, action if config changed?

    /**
     * Checks the configuration and initializes ChatWindow, loading the view.
     */
    void initialize();

    boolean onActivityResult(int requestCode, int resultCode, Intent data); // needed for file upload

    void setEventsListener(ChatWindowEventsListener eventListener);

    void reload(Boolean fullReload);

    boolean onBackPressed();

    void showChatWindow();

    void hideChatWindow();

    boolean isChatLoaded();

    boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}
