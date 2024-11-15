package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.livechatinc.chatwidget.src.extensions.buildChatUrl
import com.livechatinc.chatwidget.src.extensions.fileChooserMode
import com.livechatinc.chatwidget.src.models.ChatMessage
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
import com.livechatinc.chatwidget.src.models.ChatWidgetUrls
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class ChatWidgetPresenter internal constructor(private var view: ChatWidgetViewInternal) {
    private var listener: ChatWidgetCallbackListener? = null
    private val url = "https://cdn.livechatinc.com/app/mobile/urls.json"

    fun init(config: ChatWidgetConfig) {
        runBlocking {
            val chatUrl = NetworkHelper.fetchChatUrl(url).buildChatUrl(config)
            view.loadUrl(chatUrl)
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
            //TODO: test if this needs UI thread
            listener!!.onFileChooserActivityNotFound()
        }
    }
}

object NetworkHelper {
    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun fetchChatUrl(url: String): String {
        return withContext(Dispatchers.IO) {
            val urls: ChatWidgetUrls = client.get(url).body()

            return@withContext urls.chatUrl!!
        }
    }
}
