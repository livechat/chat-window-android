package com.livechatinc.chatsdk.src.domain.presenters

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.livechatinc.chatsdk.BuildConfig
import com.livechatinc.chatsdk.LiveChat
import com.livechatinc.chatsdk.src.utils.Logger
import com.livechatinc.chatsdk.src.domain.common.WebHttpException
import com.livechatinc.chatsdk.src.domain.common.WebResourceException
import com.livechatinc.chatsdk.src.data.domain.NetworkClient
import com.livechatinc.chatsdk.src.domain.interfaces.LiveChatViewInternal
import com.livechatinc.chatsdk.src.utils.extensions.buildChatUrl
import com.livechatinc.chatsdk.src.utils.extensions.fileChooserMode
import com.livechatinc.chatsdk.src.domain.interfaces.LiveChatViewInitListener
import com.livechatinc.chatsdk.src.domain.models.ChatMessage
import com.livechatinc.chatsdk.src.domain.models.LiveChatConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class LiveChatViewPresenter internal constructor(
    private var view: LiveChatViewInternal,
    private val networkClient: NetworkClient,
) {
    private lateinit var config: LiveChatConfig
    internal var uiReady: Boolean = false

    private var initListener: LiveChatViewInitListener? = null
    fun setInitListener(callbackListener: LiveChatViewInitListener?) {
        initListener = callbackListener
    }

    fun init(config: LiveChatConfig) {
        this.config = config

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
        return if (!BuildConfig.CHAT_URL.isNullOrBlank()) {
            BuildConfig.CHAT_URL.buildChatUrl(config)
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
}
