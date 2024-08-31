package com.livechatinc.inappchat;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public interface ChatWindowView {

    boolean setConfiguration(@NonNull ChatWindowConfiguration config); //set config, action if config changed?

    /**
     * Checks the configuration and initializes ChatWindow, loading the view.
     */
    void initialize();

    void setEventsListener(ChatWindowEventsListener eventListener);

    void reload(Boolean fullReload);

    boolean onBackPressed();

    void showChatWindow();

    void hideChatWindow();

    boolean isChatLoaded();

    boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void setUpAttachmentSupport(ActivityResultRegistry activityResultRegistry, Lifecycle lifecycle, LifecycleOwner owner);
}
