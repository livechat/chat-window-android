package com.livechatinc.chatwidget.src

import android.webkit.JavascriptInterface
import com.livechatinc.chatwidget.src.common.JsonProvider
import com.livechatinc.chatwidget.src.common.Logger
import com.livechatinc.chatwidget.src.models.BridgeMessage
import com.livechatinc.chatwidget.src.models.ChatMessage
import com.livechatinc.chatwidget.src.models.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal class LiveChatViewJSBridge internal constructor(
    private val presenter: LiveChatViewPresenter
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val json: Json = JsonProvider.instance

    @JavascriptInterface
    fun hasToken(callback: String) {
        Logger.d("### <-- hasToken async ${callback}")

        presenter.hasToken(callback)
    }

    @JavascriptInterface
    fun getToken(callback: String?) {
        Logger.d("### <-- getToken async ${callback}")

        presenter.getToken(callback)
    }

    @JavascriptInterface
    fun getFreshToken(callback: String) {
        Logger.d("### <-- getFreshToken async ${callback}")

        presenter.getFreshToken(callback)
    }

    @JavascriptInterface
    fun postMessage(messageJson: String) {
        Logger.d("### postMessage: $messageJson")
        try {
            json.decodeFromString<BridgeMessage?>(messageJson)?.let {
                dispatchMessage(it.messageType, messageJson)
            }
        } catch (cause: Exception) {
            Logger.e("Failed to decode message: $messageJson", throwable = cause)
        }
    }

    private fun dispatchMessage(type: MessageType, messageJson: String) {
        scope.launch {
            try {
                when (type) {
                    MessageType.UI_READY -> presenter.onUiReady()
                    MessageType.HIDE_CHAT_WINDOW -> presenter.onHideLiveChat()
                    MessageType.NEW_MESSAGE -> presenter.onNewMessage(
                        json.decodeFromString<ChatMessage>(messageJson)
                    )
                }
            } catch (e: Exception) {
                Logger.e("Message handling failed", throwable = e)
            }
        }
    }

    companion object {
        val INTERFACE_NAME: String = "androidMobileWidget"
    }
}
