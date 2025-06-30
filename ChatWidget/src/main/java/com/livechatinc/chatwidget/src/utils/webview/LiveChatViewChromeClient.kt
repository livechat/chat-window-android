package com.livechatinc.chatwidget.src.utils.webview

import android.net.Uri
import android.webkit.ConsoleMessage
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.livechatinc.chatwidget.src.utils.Logger
import com.livechatinc.chatwidget.src.domain.presenters.LiveChatViewPresenter

internal class LiveChatViewChromeClient(
    val presenter: LiveChatViewPresenter
) : WebChromeClient() {
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean = presenter.onShowFileChooser(filePathCallback, fileChooserParams)

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        Logger.v("onConsoleMessage: ${consoleMessage?.message()}")

        return super.onConsoleMessage(consoleMessage)
    }
}
