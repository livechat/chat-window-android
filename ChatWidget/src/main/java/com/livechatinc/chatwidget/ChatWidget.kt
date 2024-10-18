package com.livechatinc.chatwidget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.widget.FrameLayout
import com.livechatinc.chatwidget.src.ChatWidgetChromeClient
import com.livechatinc.chatwidget.src.ChatWidgetJSBridge

@SuppressLint("SetJavaScriptEnabled")
class ChatWidget(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var webView: WebView

    init {
        inflate(context, R.layout.chat_widget_internal, this)
        webView = findViewById(R.id.chat_widget_webview)

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        webView.webChromeClient = ChatWidgetChromeClient()

        webView.addJavascriptInterface(ChatWidgetJSBridge(), "androidMobileWidget")
        webView.loadUrl("https://secure.livechatinc.com/licence/11172412/v2/open_chat.cgi?groups=0&webview_widget=1");
    }
}
