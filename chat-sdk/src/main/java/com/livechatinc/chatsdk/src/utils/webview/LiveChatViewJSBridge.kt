package com.livechatinc.chatsdk.src.utils.webview

import android.webkit.JavascriptInterface
import com.livechatinc.chatsdk.src.utils.JsonProvider
import com.livechatinc.chatsdk.src.utils.Logger
import com.livechatinc.chatsdk.src.domain.models.BridgeMessage
import com.livechatinc.chatsdk.src.domain.models.ChatMessage
import com.livechatinc.chatsdk.src.domain.models.MessageType
import com.livechatinc.chatsdk.src.domain.presenters.LiveChatViewPresenter
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
