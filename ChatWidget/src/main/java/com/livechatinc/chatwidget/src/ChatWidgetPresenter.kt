package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.livechatinc.chatwidget.BuildConfig
import com.livechatinc.chatwidget.src.common.WebHttpException
import com.livechatinc.chatwidget.src.common.WebResourceException
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.extensions.buildChatUrl
import com.livechatinc.chatwidget.src.extensions.fileChooserMode
import com.livechatinc.chatwidget.src.models.ChatMessage
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
import com.livechatinc.chatwidget.src.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.models.CookieGrant
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ChatWidgetPresenter internal constructor(
    private var view: ChatWidgetViewInternal,
    private val networkClient: NetworkClient,
) {
    private var cookieGrant: CookieGrant? = null
    private lateinit var widgetToken: ChatWidgetToken
    private var listener: ChatWidgetCallbackListener? = null
    private var identityCallback: ((CookieGrant) -> Unit?)? = null
    private var config: ChatWidgetConfig? = null

    fun init(config: ChatWidgetConfig) {
        this.config = config
        this.cookieGrant = config.cookieGrant

        runBlocking {
            try {
                if (BuildConfig.CHAT_URL.isNotBlank()) {
                    view.loadUrl(BuildConfig.CHAT_URL)

                    return@runBlocking
                }

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

    fun setIdentityCallback(callback: (CookieGrant) -> Unit?) {
        identityCallback = callback
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

    fun getToken(callback: String?) {
        println("### getToken, callback: $callback, cookieGrant: $cookieGrant")
        runBlocking {
            //TODO: token refresh, fix the condition
            if (::widgetToken.isInitialized) {
                view.postWebViewMessage(
                    callback,
                    Json.encodeToString(widgetToken)
                )
            } else {
                fetchVisitorToken(callback)
            }
        }
    }

    fun getFreshToken(callback: String?) {
        println("### getFreshToken, callback: $callback")
        runBlocking {
            fetchVisitorToken(callback)
        }
    }

    private suspend fun fetchVisitorToken(callback: String?) {
        val response = networkClient.getVisitorToken(
            config!!.license,
            config!!.licenceId!!,
            config!!.clientId!!,
            cookieGrant,
        )

        widgetToken = response.token
        cookieGrant = response.cookieGrant

        view.saveTokenToPreferences(Json.encodeToString(widgetToken))
        identityCallback?.let { it(response.cookieGrant) }

        view.postWebViewMessage(
            callback,
            Json.encodeToString(widgetToken)
        )
    }

    fun hasToken(callback: String) {
        view.postWebViewMessage(callback, (::widgetToken.isInitialized).toString())
    }
}
