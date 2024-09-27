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
     * This method propagates errors and tells this window if error needs to be handled.
     *
     * @param errorType        Identifies the source of an error
     * @param errorCode        Error code,
     *                         for {@link ChatWindowErrorType#WebViewClient} see {@link WebViewClient https://developer.android.com/reference/android/webkit/WebViewClient}
     *                         for {@link ChatWindowErrorType#Console} always -1
     * @param errorDescription Description of the error
     *                         for {@link ChatWindowErrorType#WebViewClient} see {@link WebViewClient https://developer.android.com/reference/android/webkit/WebViewClient}
     *                         for {@link ChatWindowErrorType#Console} only Error level messages propagated. {@link WebChromeClient https://developer.android.com/reference/android/webkit/WebChromeClient}
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
