package com.livechatinc.chatwidget

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.webkit.WebView
import android.widget.FrameLayout
import com.livechatinc.chatwidget.src.ChatWidgetCallbackListener
import com.livechatinc.chatwidget.src.ChatWidgetChromeClient
import com.livechatinc.chatwidget.src.ChatWidgetJSBridge
import com.livechatinc.chatwidget.src.ChatWidgetPresenter
import com.livechatinc.chatwidget.src.ChatWidgetViewInternal
import com.livechatinc.chatwidget.src.ChatWidgetWebViewClient

@SuppressLint("SetJavaScriptEnabled")
class ChatWidget(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs), ChatWidgetViewInternal {
    private var webView: WebView
    private var presenter: ChatWidgetPresenter

    init {
        inflate(context, R.layout.chat_widget_internal, this)
        webView = findViewById(R.id.chat_widget_webview)

        presenter = ChatWidgetPresenter(this)

        configureWebView()
    }

    private fun configureWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        webView.webChromeClient = ChatWidgetChromeClient()
        webView.webViewClient = ChatWidgetWebViewClient()

        webView.addJavascriptInterface(
            ChatWidgetJSBridge(presenter),
            ChatWidgetJSBridge.INTERFACE_NAME
        )
    }

    fun init(licenceId: String) {
        presenter.init()
    }

    fun setCallbackListener(callbackListener: ChatWidgetCallbackListener) {
        presenter.setCallbackListener(callbackListener)
    }

    override fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    override fun launchExternalBrowser(uri: Uri) {
        TODO("Not yet implemented")
    }

    fun runOnUiThread(action: Runnable?) {
        post(action)
    }
}
