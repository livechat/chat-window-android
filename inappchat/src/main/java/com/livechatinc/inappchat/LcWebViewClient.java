package com.livechatinc.inappchat;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class LCWebViewClient extends WebViewClient {

    LCWebViewClient(ChatWindowViewImpl view, ChatWindowViewModel viewModel) {
        this.view = view;
        this.viewModel = viewModel;
    }

    final ChatWindowViewImpl view;
    final ChatWindowViewModel viewModel;

    final String TAG = WebViewClient.class.getSimpleName();

    @Override
    public void onPageFinished(WebView webView, String url) {
        if (url.startsWith("https://www.facebook.com/dialog/return/arbiter")) {
            hideWebViewPopup();
        }

        webView.setVisibility(VISIBLE);

        super.onPageFinished(webView, url);
    }

    private void hideWebViewPopup() {
        if (view.webViewPopup != null) {
            view.webViewPopup.setVisibility(GONE);
            view.removeView(view.webViewPopup);
            view.webViewPopup = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error) {
        final boolean errorHandled = viewModel.eventsListener != null && viewModel.eventsListener.onError(ChatWindowErrorType.WebViewClient, error.getErrorCode(), String.valueOf(error.getDescription()));

        view.post(() -> viewModel.onErrorDetected(
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
        final boolean errorHandled = viewModel.eventsListener != null && viewModel.eventsListener.onError(ChatWindowErrorType.WebViewClient, errorCode, description);
        view.post(() -> viewModel.onErrorDetected(errorHandled, ChatWindowErrorType.WebViewClient, errorCode, description));
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
        String uriString = uri.toString();
        Log.i(TAG, "handle url: " + uriString);
        boolean facebookLogin = uriString.matches("https://.+facebook.+(/dialog/oauth\\?|/login\\.php\\?|/dialog/return/arbiter\\?).+");

        if (facebookLogin) {
            return false;
        } else {
            hideWebViewPopup();

            String originalUrl = webView.getOriginalUrl();
            if (uriString.equals(originalUrl) || ChatWindowViewModel.isSecureLivechatIncDomain(uri.getHost())) {
                return false;
            } else {
                if (viewModel.eventsListener != null && viewModel.eventsListener.handleUri(uri)) {

                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    view.getContext().startActivity(intent);
                }

                return true;
            }
        }
    }
}