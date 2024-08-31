package com.livechatinc.inappchat;

import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

/**
 * Created by szymonjarosz on 20/07/2017.
 */

public interface ChatWindowView {


    boolean setConfiguration(@NonNull ChatWindowConfiguration config); //set config, action if config changed?

    /**
     * Checks the configuration and initializes ChatWindow, loading the view.
     */
    void initialize();

    //TODO: possibly can be removed in favor of setUpAttachmentSupport
    boolean onActivityResult(int requestCode, int resultCode, Intent data); // needed for file upload

    void setEventsListener(ChatWindowEventsListener eventListener);

    void reload(Boolean fullReload);

    boolean onBackPressed();

    void showChatWindow();

    void hideChatWindow();

    boolean isChatLoaded();

    boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void setUpAttachmentSupport(ActivityResultRegistry activityResultRegistry, Lifecycle lifecycle, LifecycleOwner owner);
}
