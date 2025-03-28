package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.livechatinc.chatwidget.BuildConfig
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.chatwidget.src.common.WebHttpException
import com.livechatinc.chatwidget.src.common.WebResourceException
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.extensions.buildChatUrl
import com.livechatinc.chatwidget.src.extensions.fileChooserMode
import com.livechatinc.chatwidget.src.models.ChatMessage
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
import com.livechatinc.chatwidget.src.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.models.CookieGrant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ChatWidgetPresenter internal constructor(
    private var view: ChatWidgetViewInternal,
    private val networkClient: NetworkClient,
) {
    private var cookieGrant: CookieGrant? = null
    private lateinit var widgetToken: ChatWidgetToken
    private var listener: LiveChatViewCallbackListener? = null
    private var identityCallback: ((CookieGrant) -> Unit?)? = null
    private lateinit var config: ChatWidgetConfig

    fun init(config: ChatWidgetConfig) {
        this.config = config
        this.cookieGrant = config.cookieGrant

        identityCallback = LiveChat.getInstance().identityCallback

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val chatUrl = chatUrl()

                withContext(Dispatchers.Main) {
                    view.loadUrl(chatUrl)
                }

                return@launch
            } catch (cause: Throwable) {
                println("### ChatWidgetPresenter.init: $cause")
                listener?.onError(cause)
                cause.printStackTrace()
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

    fun setCallbackListener(callbackListener: LiveChatViewCallbackListener?) {
        listener = callbackListener
    }

    fun onUiReady() {
        listener?.onLoaded()
    }

    fun onHideChatWidget() {
        listener?.onHide()
    }

    fun onNewMessage(message: ChatMessage?) {
        listener?.onNewMessage(message)
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
        view.postWebViewMessage(callback, (::widgetToken.isInitialized).toString())
    }

    fun getToken(callback: String?) {
        if (::widgetToken.isInitialized) {
            view.postWebViewMessage(
                callback,
                Json.encodeToString(widgetToken)
            )
        } else {
            getFreshToken(callback)
        }
    }

    fun getFreshToken(callback: String?) {
        if (config.isCIPEnabled) {
            CoroutineScope(Dispatchers.IO).launch {
                fetchVisitorToken(callback)
            }
        } else {
            //TODO: decide how to deal with disabled CIP
            view.postWebViewMessage(
                callback,
                Json.encodeToString("")
            )
        }
    }

    private suspend fun fetchVisitorToken(callback: String?) {
        val response = networkClient.getVisitorToken(
            config.license,
            config.licenceId!!,
            config.clientId!!,
            cookieGrant,
        )

        widgetToken = response.token
        cookieGrant = response.cookieGrant

//        view.saveTokenToPreferences(Json.encodeToString(widgetToken))
        identityCallback?.let { it(response.cookieGrant) }

        view.postWebViewMessage(
            callback,
            Json.encodeToString(widgetToken)
        )
    }
}
