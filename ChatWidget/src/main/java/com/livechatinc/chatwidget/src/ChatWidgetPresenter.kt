package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.livechatinc.chatwidget.BuildConfig
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.chatwidget.src.common.Logger
import com.livechatinc.chatwidget.src.common.WebHttpException
import com.livechatinc.chatwidget.src.common.WebResourceException
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.extensions.buildChatUrl
import com.livechatinc.chatwidget.src.extensions.fileChooserMode
import com.livechatinc.chatwidget.src.models.ChatMessage
import com.livechatinc.chatwidget.src.models.LiveChatConfig
import com.livechatinc.chatwidget.src.models.IdentityGrant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ChatWidgetPresenter internal constructor(
    private var view: ChatWidgetViewInternal,
    private val networkClient: NetworkClient,
) {
    private var identityGrant: IdentityGrant? = null
    private var listener: LiveChatViewCallbackListener? = null
    private lateinit var config: LiveChatConfig
    private val eventDispatcher = LiveChatEventDispatcher()

    fun addCallbackListener(listener: LiveChatViewCallbackListener) {
        eventDispatcher.addListener(listener)
    }

    fun removeCallbackListener(listener: LiveChatViewCallbackListener) {
        eventDispatcher.removeListener(listener)
    }

    fun init(config: LiveChatConfig) {
        this.config = config
        this.identityGrant = config.customIdentityConfig?.identityGrant

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val chatUrl = chatUrl()

                withContext(Dispatchers.Main) {
                    view.loadUrl(chatUrl)
                }

                return@launch
            } catch (cause: Throwable) {
                Logger.e("Failed to load chat url: $cause", throwable = cause)

                withContext(Dispatchers.Main) {
                    listener?.onError(cause)
                }
            }
        }
    }

    private suspend fun chatUrl(): String {
        return if (BuildConfig.CHAT_URL != null && BuildConfig.CHAT_URL.isNotBlank()) {
            BuildConfig.CHAT_URL
        } else {
            networkClient.fetchChatUrl().buildChatUrl(config)
        }
    }

    fun onUiReady() {
        eventDispatcher.dispatchOnLoaded()
    }

    fun onHideChatWidget() {
        eventDispatcher.dispatchOnHide()
    }

    fun onNewMessage(message: ChatMessage?) {
        eventDispatcher.dispatchOnNewMessage(message)
    }

    fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        view.startFilePicker(filePathCallback, fileChooserParams.fileChooserMode())

        return true
    }

    fun onFileChooserActivityNotFound() {
        eventDispatcher.dispatchOnFileChooserActivityNotFound()
    }

    fun onWebResourceError(code: Int, description: String, failingUrl: String) {
        eventDispatcher.dispatchOnError(WebResourceException(code, description, failingUrl))
        printWebViewError(
            code,
            description,
            failingUrl
        )
    }

    fun onWebViewHttpError(code: Int, description: String, failingUrl: String) {
        eventDispatcher.dispatchOnError(WebHttpException(code, description, failingUrl))
        printWebViewError(
            code,
            description,
            failingUrl
        )
    }

    private fun printWebViewError(errorCode: Int?, description: String?, failingUrl: String?) {
        Logger.e("WebViewError, code: $errorCode, description: $description, failingUrl: $failingUrl")
    }

    //TODO: check main thread safety
    fun handleUrl(uri: Uri?): Boolean {
        //TODO bring back opening links in external browser
        return false
//        if (uri == null) {
//            return false
//        }
//
//        //TODO: test dal/fra licences
//        view.launchExternalBrowser(uri)
//
//        return true
    }

    fun hasToken(callback: String) {
        val hasToken = LiveChat.getInstance().hasToken();

        view.postWebViewMessage(callback, (hasToken).toString())
    }

    fun getToken(callback: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = LiveChat.getInstance().getToken()

            //TODO: deal with a case where token is returned after view/webView was destroyed
            view.postWebViewMessage(
                callback,
                Json.encodeToString(token)
            )
        }
    }

    fun getFreshToken(callback: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = LiveChat.getInstance().getFreshToken()

            view.postWebViewMessage(
                callback,
                Json.encodeToString(token)
            )
        }
    }
}
