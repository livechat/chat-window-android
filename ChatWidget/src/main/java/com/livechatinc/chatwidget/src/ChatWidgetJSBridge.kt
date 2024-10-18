package com.livechatinc.chatwidget.src

import android.webkit.JavascriptInterface
import org.json.JSONObject

internal class ChatWidgetJSBridge internal constructor(
        private val presenter: ChatWidgetPresenter
) {
    private val KEY_MESSAGE_TYPE = "messageType"
    private val TYPE_UI_READY = "uiReady"
    private val TYPE_HIDE_CHAT_WINDOW = "hideChatWindow"
    private val TYPE_NEW_MESSAGE = "newMessage"

    @JavascriptInterface
    fun postMessage(messageJson: String) {
        println("### postMessage: $messageJson")
        val jsonObject = JSONObject(messageJson)
        if (jsonObject.has(KEY_MESSAGE_TYPE)) {
            dispatchMessage(jsonObject.getString(KEY_MESSAGE_TYPE), messageJson)
        }
    }

    private fun dispatchMessage(type: String, messageJson: String) {
        when (type) {
            TYPE_HIDE_CHAT_WINDOW -> presenter.onHideChatWidget()
            TYPE_UI_READY -> presenter.onUiReady()
            TYPE_NEW_MESSAGE -> {}
        }
    }

    companion object {
        val INTERFACE_NAME: String = "androidMobileWidget"
    }
}
