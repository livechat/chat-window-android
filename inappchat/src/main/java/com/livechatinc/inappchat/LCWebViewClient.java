package com.livechatinc.inappchat;


import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class LCWebViewClient extends WebViewClient {

    LCWebViewClient(ChatWindowPresenter presenter) {
        this.presenter = presenter;
    }

    final ChatWindowPresenter presenter;

    @Override
    public void onPageFinished(WebView webView, String url) {
        presenter.onPageLoaded();

        super.onPageFinished(webView, url);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        if (request.isForMainFrame()) {
            presenter.onErrorDetected(
                    ChatWindowErrorType.WebViewClient,
                    errorResponse.getStatusCode(),
                    errorResponse.getReasonPhrase()
            );
        }

        super.onReceivedHttpError(view, request, errorResponse);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error) {
        if (request.isForMainFrame()) {
            presenter.onErrorDetected(
                    ChatWindowErrorType.WebViewClient,
                    error.getErrorCode(),
                    String.valueOf(error.getDescription())
            );
        }

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
