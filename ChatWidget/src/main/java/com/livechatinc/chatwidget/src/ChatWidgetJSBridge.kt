package com.livechatinc.chatwidget.src

import android.webkit.JavascriptInterface
import com.livechatinc.chatwidget.src.models.BridgeMessage
import com.livechatinc.chatwidget.src.models.ChatMessage
import com.livechatinc.chatwidget.src.models.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal class ChatWidgetJSBridge internal constructor(
    private val presenter: ChatWidgetPresenter
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    //TODO: single json instance
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @JavascriptInterface
    fun hasToken(callback: String) {
        println("### <-- hasToken async ${callback}")

        presenter.hasToken(callback)
    }

    @JavascriptInterface
    fun getToken(callback: String?) {
        println("### <-- getToken async ${callback}")

        presenter.getToken(callback)
    }

    @JavascriptInterface
    fun getFreshToken(callback: String) {
        println("### <-- getFreshToken async ${callback}")

        presenter.getFreshToken(callback)
    }

    @JavascriptInterface
    fun postMessage(messageJson: String) {
        println("### postMessage: $messageJson")

        //TODO: handle exceptions
        val messageType = json.decodeFromString<BridgeMessage>(messageJson)

        dispatchMessage(messageType.messageType, messageJson)
    }

    private fun dispatchMessage(type: MessageType, messageJson: String) {
        scope.launch {
            try {
                when (type) {
                    MessageType.UI_READY -> presenter.onUiReady()
                    MessageType.HIDE_CHAT_WINDOW -> presenter.onHideChatWidget()
                    MessageType.NEW_MESSAGE -> presenter.onNewMessage(
                        json.decodeFromString<ChatMessage>(messageJson)
                    )
                }
            } catch (e: Exception) {
                println("Message handling failed: $e")
            }
        }
    }

    companion object {
        val INTERFACE_NAME: String = "androidMobileWidget"
    }
}
