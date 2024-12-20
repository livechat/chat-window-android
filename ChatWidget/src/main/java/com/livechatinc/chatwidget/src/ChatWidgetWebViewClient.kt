package com.livechatinc.chatwidget.src

import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

internal class ChatWidgetWebViewClient(val presenter: ChatWidgetPresenter) : WebViewClient() {
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
