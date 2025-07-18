package com.livechatinc.chatwidget.src.domain.presenters

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.livechatinc.chatwidget.BuildConfig
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.chatwidget.src.utils.Logger
import com.livechatinc.chatwidget.src.domain.common.WebHttpException
import com.livechatinc.chatwidget.src.domain.common.WebResourceException
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.domain.interfaces.LiveChatViewInternal
import com.livechatinc.chatwidget.src.utils.extensions.buildChatUrl
import com.livechatinc.chatwidget.src.utils.extensions.fileChooserMode
import com.livechatinc.chatwidget.src.domain.interfaces.LiveChatViewInitListener
import com.livechatinc.chatwidget.src.domain.models.ChatMessage
import com.livechatinc.chatwidget.src.domain.models.LiveChatConfig
import com.livechatinc.chatwidget.src.domain.models.IdentityGrant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class LiveChatViewPresenter internal constructor(
    private var view: LiveChatViewInternal,
    private val networkClient: NetworkClient,
) {
    private var identityGrant: IdentityGrant? = null
    private lateinit var config: LiveChatConfig
    internal var uiReady: Boolean = false

    // Init callback listener
    private var initListener: LiveChatViewInitListener? = null
    fun setInitListener(callbackListener: LiveChatViewInitListener?) {
        initListener = callbackListener
    }

    fun init(config: LiveChatConfig) {
        Logger.d("### ChatWidgetPresenter.init()")
        this.config = config
        this.identityGrant = config.customIdentityConfig?.identityGrant

        if (!uiReady) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val chatUrl = chatUrl()

                    withContext(Dispatchers.Main) {
                        view.loadUrl(chatUrl)
                    }

                    return@launch
                } catch (cause: Throwable) {
                    withContext(Dispatchers.Main) {
                        onError(cause)
                    }
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
    }

    internal fun onHideLiveChat() {
        initListener?.onHide()
    }

    private fun onError(cause: Throwable) {
        initListener?.onError(cause)
        LiveChat.getInstance().errorListener?.onError(cause)

        Logger.e("${cause.javaClass}, ${cause.message}", throwable = cause)
    }

    internal fun onNewMessage(message: ChatMessage?) {
        LiveChat.getInstance().newMessageListener?.onNewMessage(message, view.isChatShown())
    }

    internal fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        view.startFilePicker(filePathCallback, fileChooserParams.fileChooserMode())

        return true
    }

    internal fun onFileChooserActivityNotFound() {
        LiveChat.getInstance().fileChooserNotFoundListener?.onFileChooserActivityNotFound()
    }

    internal fun onWebResourceError(code: Int, description: String, failingUrl: String) {
        onError(WebResourceException(code, description, failingUrl))
    }

    internal fun onWebViewHttpError(code: Int, description: String, failingUrl: String) {
        onError(WebHttpException(code, description, failingUrl))
    }

    fun handleUrl(uri: Uri?): Boolean {
        uri ?: return false

        LiveChat.getInstance().urlHandler?.let { handler ->
            if (handler.handleUrl(uri)) {
                return true
            }
        }

        view.launchExternalBrowser(uri)

        return true
    }

    fun hasToken(callback: String) {
        val hasToken = LiveChat.getInstance().hasToken()

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
