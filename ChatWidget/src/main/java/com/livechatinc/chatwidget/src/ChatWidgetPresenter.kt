package com.livechatinc.chatwidget.src

import com.livechatinc.chatwidget.ChatWidget
import com.livechatinc.chatwidget.src.models.ChatMessage

internal class ChatWidgetPresenter internal constructor(private var view: ChatWidget) {
    private var listener: ChatWidgetCallbackListener? = null

    fun init() {
        view.loadUrl("https://secure.livechatinc.com/licence/11172412/v2/open_chat.cgi?groups=0&webview_widget=1")
    }

    fun setCallbackListener(callbackListener: ChatWidgetCallbackListener) {
        listener = callbackListener
    }

    fun onUiReady() {
        if (listener != null) {
            view.runOnUiThread(listener!!::chatLoaded)
        }
    }

    fun onHideChatWidget() {
        if (listener != null) {
            view.runOnUiThread(listener!!::hideChatWidget)
        }
    }

    fun onNewMessage(message: ChatMessage?) {
        TODO("Not yet implemented")
    }
}
