package com.livechatinc.chatwidget.src

import android.webkit.JavascriptInterface
import com.livechatinc.chatwidget.src.models.BridgeMessage
import com.livechatinc.chatwidget.src.models.ChatMessage
import com.livechatinc.chatwidget.src.models.MessageType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class ChatWidgetJSBridge internal constructor(
    private val presenter: ChatWidgetPresenter
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @JavascriptInterface
    fun postMessage(messageJson: String) {
        println("### postMessage: $messageJson")

        val messageType = json.decodeFromString<BridgeMessage>(messageJson)

        dispatchMessage(messageType.messageType, messageJson)
    }

    private fun dispatchMessage(type: MessageType, messageJson: String) {
        when (type) {
            MessageType.UI_READY -> presenter.onUiReady()
            MessageType.HIDE_CHAT_WINDOW -> presenter.onHideChatWidget()
            MessageType.NEW_MESSAGE -> presenter.onNewMessage(
                json.decodeFromString<ChatMessage>(messageJson)
            )
        }
    }

    companion object {
        val INTERFACE_NAME: String = "androidMobileWidget"
    }
}
