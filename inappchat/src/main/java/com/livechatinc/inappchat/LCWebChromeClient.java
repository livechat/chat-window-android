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

    LCWebChromeClient(ChatWindowViewImpl view, ChatWindowPresenter presenter) {
        this.view = view;
        this.presenter = presenter;
    }

    final ChatWindowViewImpl view;
    final ChatWindowPresenter presenter;

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
        presenter.eventsListener.onRequestAudioPermissions(runtimePermissions, REQUEST_CODE_AUDIO_PERMISSIONS);
    }

    @Override
    public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
        if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
            presenter.onErrorDetected(ChatWindowErrorType.Console, -1, consoleMessage.message());
        }

        return super.onConsoleMessage(consoleMessage);
    }
}
