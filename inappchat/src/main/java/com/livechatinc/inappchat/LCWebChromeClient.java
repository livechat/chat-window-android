package com.livechatinc.inappchat;


import static com.livechatinc.inappchat.ChatWindowViewImpl.REQUEST_CODE_AUDIO_PERMISSIONS;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;


class LCWebChromeClient extends WebChromeClient {

    LCWebChromeClient(ChatWindowViewImpl view, ChatWindowController controller) {
        this.view = view;
        this.controller = controller;
    }

    final ChatWindowViewImpl view;
    final ChatWindowController controller;

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        view.chooseUriToUpload(uploadMsg);
    }

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        view.chooseUriToUpload(uploadMsg);
    }

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        view.chooseUriToUpload(uploadMsg);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
        view.chooseUriArrayToUpload(uploadMsg);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequest(final PermissionRequest request) {
        view.webRequestPermissions = request;
        String[] runtimePermissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.MODIFY_AUDIO_SETTINGS};
        controller.eventsListener.onRequestAudioPermissions(runtimePermissions, REQUEST_CODE_AUDIO_PERMISSIONS);
    }

    @Override
    public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
        if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
            controller.onErrorDetected(ChatWindowErrorType.Console, -1, consoleMessage.message());
        }

        return super.onConsoleMessage(consoleMessage);
    }
}
