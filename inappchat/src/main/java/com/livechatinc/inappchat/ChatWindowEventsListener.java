package com.livechatinc.inappchat;

import android.net.Uri;

import com.livechatinc.inappchat.models.NewMessageModel;

public interface ChatWindowEventsListener {
    /**
     * Called when window successfully initialized.
     */
    void onWindowInitialized();

    /**
     * Triggered when user minimizes a chat inside the window or closes it via back button and when
     * {@link ChatWindowView#showChatWindow()} or {@link ChatWindowView#hideChatWindow()} are used.
     */
    void onChatWindowVisibilityChanged(boolean visible);

    /**
     * Happens every time chat is loaded and new message appears.
     */
    void onNewMessage(NewMessageModel message, boolean windowVisible);

    /**
     * Needed for requesting AUDIO and CAMERA permissions for SnapCall integration
     */
    void onRequestAudioPermissions(String[] permissions, int requestCode);

    /**
     * This callback notifies about errors. See {@link ChatWindowErrorType} for more details.
     *
     * @param errorType        Identifies the source of an error
     * @param errorCode        Error code
     * @param errorDescription Description of the error
     * @return true if error handled. Returning false, means that library should handle error - show error view
     */
    boolean onError(ChatWindowErrorType errorType, int errorCode, String errorDescription);

    /**
     * Return true to disable default uri handling.
     */
    boolean handleUri(final Uri uri);

    /**
     * Notifies about window not able to lunch an app to handle file picker intent.
     */
    void onFilePickerActivityNotFound();
}
