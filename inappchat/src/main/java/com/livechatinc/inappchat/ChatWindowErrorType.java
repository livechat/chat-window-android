package com.livechatinc.inappchat;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultRegistry;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public enum ChatWindowErrorType {

    /**
     * Errors detected by WebViewClient reported only when related to the main frame.
     *
     * @see android.webkit.WebViewClient#onReceivedHttpError(WebView, WebResourceRequest, WebResourceResponse)
     * @see android.webkit.WebViewClient#onReceivedError(WebView, WebResourceRequest, WebResourceError)
     */
    WebViewClient,

    /**
     * Errors while loading initial chat window configuration
     */
    InitialConfiguration,

    /**
     * Error indicating missing file sharing support
     *
     * @see ChatWindowView#supportFileSharing(ActivityResultRegistry, Lifecycle, LifecycleOwner)
     */
    NoFileSharingSupport,
}
