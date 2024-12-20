package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.livechatinc.chatwidget.src.common.WebHttpException
import com.livechatinc.chatwidget.src.common.WebResourceException
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

    fun onWebResourceError(code: Int, description: String, failingUrl: String) {
        listener?.onError(WebResourceException(code, description, failingUrl))
        printWebViewError(
            code,
            description,
            failingUrl
        )
    }

    fun onWebViewHttpError(code: Int, description: String, failingUrl: String) {
        listener?.onError(WebHttpException(code, description, failingUrl))
        printWebViewError(
            code,
            description,
            failingUrl
        )
    }

    private fun printWebViewError(errorCode: Int?, description: String?, failingUrl: String?) {
        println("Error, code: $errorCode, description: $description, failingUrl: $failingUrl")
    }
}
