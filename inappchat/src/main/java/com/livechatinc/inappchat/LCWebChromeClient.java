package com.livechatinc.inappchat;


import static com.livechatinc.inappchat.ChatWindowViewImpl.REQUEST_CODE_AUDIO_PERMISSIONS;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;


class LCWebChromeClient extends WebChromeClient {

    LCWebChromeClient(ChatWindowViewImpl view, ChatWindowViewModel viewModel) {
        this.view = view;
        this.viewModel = viewModel;
    }

    final ChatWindowViewImpl view;
    final ChatWindowViewModel viewModel;

    private static final String TAG = WebChromeClient.class.getSimpleName();

    @Override
    public boolean onCreateWindow(
            WebView webView,
            boolean isDialog,
            boolean isUserGesture,
            Message resultMsg
    ) {
        view.webViewPopup = new WebView(view.getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(view.webViewPopup, true);
        }

        view.webViewPopup.setVerticalScrollBarEnabled(false);
        view.webViewPopup.setHorizontalScrollBarEnabled(false);
        view.webViewPopup.setWebViewClient(new LCWebViewClient(view, viewModel));
        view.webViewPopup.getSettings().setJavaScriptEnabled(true);
        view.webViewPopup.getSettings().setSavePassword(false);
        view.webViewPopup.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        view.addView(view.webViewPopup);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(view.webViewPopup);
        resultMsg.sendToTarget();

        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
        Log.d(TAG, "onCloseWindow");
    }

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
        viewModel.eventsListener.onRequestAudioPermissions(runtimePermissions, REQUEST_CODE_AUDIO_PERMISSIONS);
    }

    @Override
    public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
        if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
            final boolean errorHandled = viewModel.eventsListener != null && viewModel.eventsListener.onError(ChatWindowErrorType.Console, -1, consoleMessage.message());
            view.post(() -> viewModel.onErrorDetected(errorHandled, ChatWindowErrorType.Console, -1, consoleMessage.message()));
        }
        Log.i(TAG, "onConsoleMessage" + consoleMessage.messageLevel().name() + " " + consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }
}
