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
    internal var uiReady: Boolean = false

    //Init callback listener
    private var initListener: LiveChatViewInitCallbackListener? = null
    fun setInitCallbackListener(callbackListener: LiveChatViewInitCallbackListener) {
        initListener = callbackListener
    }

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
                //TODO: potentially redundant error log message
                Logger.e("Failed to load chat url: $cause", throwable = cause)

                withContext(Dispatchers.Main) {
                    onError(cause)
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

    internal fun onUiReady() {
        uiReady = true
        initListener?.onUIReady()
        eventDispatcher.dispatchOnLoaded()
    }

    internal fun onHideChatWidget() {
        initListener?.onHide()
        eventDispatcher.dispatchOnHide()
    }

    private fun onError(cause: Throwable) {
        initListener?.onError(cause)

        Logger.e("${cause.javaClass}, ${cause.message}")
    }

    internal fun onNewMessage(message: ChatMessage?) {
        LiveChat.getInstance().newMessageListener?.onNewMessage(message)
        eventDispatcher.dispatchOnNewMessage(message)
    }

    internal fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        view.startFilePicker(filePathCallback, fileChooserParams.fileChooserMode())

        return true
    }

    internal fun onFileChooserActivityNotFound() {
        eventDispatcher.dispatchOnFileChooserActivityNotFound()
    }

    internal fun onWebResourceError(code: Int, description: String, failingUrl: String) {
        onError(WebResourceException(code, description, failingUrl))
    }

    internal fun onWebViewHttpError(code: Int, description: String, failingUrl: String) {
        onError(WebResourceException(code, description, failingUrl))
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
