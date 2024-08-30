package com.livechatinc.inappchat;


import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class LCWebViewClient extends WebViewClient {

    LCWebViewClient(ChatWindowController controller) {
        this.controller = controller;
    }

    final ChatWindowController controller;

    final String TAG = WebViewClient.class.getSimpleName();

    @Override
    public void onPageFinished(WebView webView, String url) {
        controller.onPageLoaded();

        super.onPageFinished(webView, url);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error) {
        final boolean errorHandled = controller.eventsListener != null && controller.eventsListener.onError(ChatWindowErrorType.WebViewClient, error.getErrorCode(), String.valueOf(error.getDescription()));

        view.post(() -> controller.onErrorDetected(
                errorHandled,
                ChatWindowErrorType.WebViewClient,
                error.getErrorCode(),
                String.valueOf(error.getDescription())
        ));

        super.onReceivedError(view, request, error);
        Log.e(TAG, "onReceivedError: " + error.getErrorCode() + ": desc: " + error.getDescription() + " url: " + request.getUrl());
    }

    @Override
    public void onReceivedError(WebView view, final int errorCode, final String description, String failingUrl) {
        final boolean errorHandled = controller.eventsListener != null && controller.eventsListener.onError(ChatWindowErrorType.WebViewClient, errorCode, description);
        view.post(() -> controller.onErrorDetected(errorHandled, ChatWindowErrorType.WebViewClient, errorCode, description));
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.e(TAG, "onReceivedError: " + errorCode + ": desc: " + description + " url: " + failingUrl);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Uri uri = Uri.parse(url);
        return handleUri(view, uri);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        final Uri uri = request.getUrl();
        return handleUri(view, uri);
    }

    private boolean handleUri(WebView webView, final Uri uri) {
        return controller.handleUri(uri, webView.getOriginalUrl());
    }
}
