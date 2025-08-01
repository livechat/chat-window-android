package com.livechatinc.inappchat;


import static com.livechatinc.inappchat.ChatWindowViewImpl.REQUEST_CODE_AUDIO_PERMISSIONS;

import android.Manifest;
import android.net.Uri;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


class LCWebChromeClient extends WebChromeClient {

    LCWebChromeClient(ChatWindowViewImpl view, ChatWindowPresenter presenter) {
        this.view = view;
        this.presenter = presenter;
    }

    final ChatWindowViewImpl view;
    final ChatWindowPresenter presenter;

    @Override
    public boolean onShowFileChooser(
            WebView webView,
            ValueCallback<Uri[]> uploadMsg,
            FileChooserParams fileChooserParams
    ) {
        view.chooseUriArrayToUpload(uploadMsg, toInternalMode(fileChooserParams.getMode()));
        return true;
    }

    private FileChooserMode toInternalMode(int mode) {
        return mode == FileChooserParams.MODE_OPEN_MULTIPLE ? FileChooserMode.MULTIPLE : FileChooserMode.SINGLE;
    }

    @Override
    public void onPermissionRequest(final PermissionRequest request) {
        view.webRequestPermissions = request;
        String[] runtimePermissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
        };
        presenter.eventsListener.onRequestAudioPermissions(runtimePermissions, REQUEST_CODE_AUDIO_PERMISSIONS);
    }
}
