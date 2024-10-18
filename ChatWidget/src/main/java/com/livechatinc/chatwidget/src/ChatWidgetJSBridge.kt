package com.livechatinc.chatwidget.src

import android.webkit.JavascriptInterface
import com.google.gson.GsonBuilder
import com.livechatinc.chatwidget.src.models.ChatMessage
import org.json.JSONObject

internal class ChatWidgetJSBridge internal constructor(
        private val presenter: ChatWidgetPresenter
) {
    private val KEY_MESSAGE_TYPE = "messageType"
    val gson = GsonBuilder().create()

    @JavascriptInterface
    fun postMessage(messageJson: String) {
        println("### postMessage: $messageJson")
        val jsonObject = JSONObject(messageJson)
        if (jsonObject.has(KEY_MESSAGE_TYPE)) {
            //TODO: Allow for unknown types
            val messageType = gson.fromJson(
                    jsonObject.getString(KEY_MESSAGE_TYPE),
                    MessageType::class.java
            )

            dispatchMessage(messageType, messageJson)
        }
    }

    private fun dispatchMessage(type: MessageType, messageJson: String) {
        when (type) {
            MessageType.UI_READY -> presenter.onUiReady()
            MessageType.HIDE_CHAT_WINDOW -> presenter.onHideChatWidget()
            MessageType.TYPE_NEW_MESSAGE -> presenter.onNewMessage(
                    gson.fromJson(messageJson, ChatMessage::class.java)
            )
        }
    }

    companion object {
        val INTERFACE_NAME: String = "androidMobileWidget"
    }
}
