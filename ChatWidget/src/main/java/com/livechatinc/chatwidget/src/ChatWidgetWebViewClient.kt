package com.livechatinc.chatwidget.src

import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

class ChatWidgetWebViewClient : WebViewClient() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        printWebViewError(
            error?.errorCode,
            error?.description?.toString(),
            request?.url?.toString()
        )

        super.onReceivedError(view, request, error)
    }


    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
    }


    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        printWebViewError(
            errorResponse?.statusCode,
            errorResponse?.reasonPhrase,
            request?.url?.toString()
        )

        super.onReceivedHttpError(view, request, errorResponse)
    }


    private fun printWebViewError(errorCode: Int?, description: String?, failingUrl: String?) {
        println("Error, code: $errorCode, description: $description, failingUrl: $failingUrl")
    }
}
