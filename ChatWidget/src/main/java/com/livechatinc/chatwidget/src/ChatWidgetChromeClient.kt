package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

internal class ChatWidgetChromeClient(val widget: ChatWidgetViewInternal) : WebChromeClient() {
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        //TODO: support single and multi file mode
        widget.startFilePicker(filePathCallback)

        return true
    }
}
