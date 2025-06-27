package com.livechatinc.chatwidget.src

import android.net.Uri
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

internal class LiveChatViewWebViewClient(private val presenter: LiveChatViewPresenter) :
    WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return presenter.handleUrl(request?.url)
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return presenter.handleUrl(Uri.parse(url))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        if (request.isForMainFrame) {
            presenter.onWebResourceError(
                error.errorCode,
                error.description.toString(),
                request.url.toString()
            )
        }

        super.onReceivedError(view, request, error)
    }


    @Deprecated("Deprecated in Java")
    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        presenter.onWebResourceError(errorCode, description, failingUrl)

        super.onReceivedError(view, errorCode, description, failingUrl)
    }


    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse?
    ) {
        if (request.isForMainFrame) {
            presenter.onWebViewHttpError(
                errorResponse!!.statusCode,
                errorResponse.reasonPhrase,
                request.url.toString()
            )
        }

        super.onReceivedHttpError(view, request, errorResponse)
    }
}
