package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.extensions.buildChatUrl
import com.livechatinc.chatwidget.src.extensions.fileChooserMode
import com.livechatinc.chatwidget.src.models.ChatMessage
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
import kotlinx.coroutines.runBlocking

internal class ChatWidgetPresenter internal constructor(
    private var view: ChatWidgetViewInternal,
    private val networkClient: NetworkClient
) {
    private var listener: ChatWidgetCallbackListener? = null

    fun init(config: ChatWidgetConfig) {
        runBlocking {
            try {
                val chatUrl = networkClient.fetchChatUrl().buildChatUrl(config)

                view.loadUrl(chatUrl)
            } catch (cause: Throwable) {
                println("### ChatWidgetPresenter.init: $cause")
                listener?.onError(cause)
                cause.printStackTrace()
            }
        }
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
        if (listener != null) {
            view.runOnUiThread { listener!!.onChatMessage(message) }
        }
    }

    fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        view.startFilePicker(filePathCallback, fileChooserParams.fileChooserMode())

        return true
    }

    fun onFileChooserActivityNotFound() {
        if (listener != null) {
            listener!!.onFileChooserActivityNotFound()
        }
    }
}
