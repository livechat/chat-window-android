package com.livechatinc.inappchat;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public interface ChatWindowView {

    /**
     * Initializes ChatWindow, loading the view with provided configuration.
     */
    void init(@NonNull ChatWindowConfiguration config);

    void setEventsListener(ChatWindowEventsListener eventListener);

    void reload();

    boolean onBackPressed();

    void showChatWindow();

    void hideChatWindow();

    boolean isChatLoaded();

    boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void supportFileSharing(
            ActivityResultRegistry activityResultRegistry,
            Lifecycle lifecycle,
            LifecycleOwner owner
    );
}
