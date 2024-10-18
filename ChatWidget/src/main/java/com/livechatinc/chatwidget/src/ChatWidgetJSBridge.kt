package com.livechatinc.chatwidget.src

import android.webkit.JavascriptInterface

internal class ChatWidgetJSBridge {
    @JavascriptInterface
    fun postMessage(messageJson: String) {
        println("### postMessage: $messageJson")
    }


}
