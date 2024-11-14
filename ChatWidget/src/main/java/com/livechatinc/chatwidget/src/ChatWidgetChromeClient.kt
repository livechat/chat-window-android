package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.livechatinc.chatwidget.ChatWidget

internal class ChatWidgetChromeClient(val widget: ChatWidget) : WebChromeClient() {
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
