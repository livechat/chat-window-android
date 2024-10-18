package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

internal class ChatWidgetChromeClient : WebChromeClient() {
    override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
    ): Boolean {
        println("### onShowFileChooser: $webView, $filePathCallback, $fileChooserParams")
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }
}
