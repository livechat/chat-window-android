package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ConsoleMessage
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

internal class ChatWidgetChromeClient(
    val presenter: ChatWidgetPresenter
) : WebChromeClient() {
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean = presenter.onShowFileChooser(filePathCallback, fileChooserParams)

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        println("### onConsoleMessage: ${consoleMessage?.message()}")

        return super.onConsoleMessage(consoleMessage)
    }
}
