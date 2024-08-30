package com.livechatinc.inappchat;


import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.livechatinc.inappchat.models.ChatWindowErrorType;

class LCWebViewClient extends WebViewClient {

    LCWebViewClient(ChatWindowPresenter presenter) {
        this.presenter = presenter;
    }

    final ChatWindowPresenter presenter;

    final String TAG = WebViewClient.class.getSimpleName();

    @Override
    public void onPageFinished(WebView webView, String url) {
        presenter.onPageLoaded();

        super.onPageFinished(webView, url);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error) {

        presenter.onErrorDetected(
                ChatWindowErrorType.WebViewClient,
                error.getErrorCode(),
                String.valueOf(error.getDescription())
        );

        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedError(WebView view, final int errorCode, final String description, String failingUrl) {
        presenter.onErrorDetected(ChatWindowErrorType.WebViewClient, errorCode, description);

        super.onReceivedError(view, errorCode, description, failingUrl);
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
        return presenter.handleUri(uri, webView.getOriginalUrl());
    }
}
