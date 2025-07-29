package com.livechatinc.chatsdk.src.data.core

import com.livechatinc.chatsdk.src.domain.models.BuildInfo
import com.livechatinc.chatsdk.src.data.domain.NetworkClient
import com.livechatinc.chatsdk.src.domain.models.ChatWidgetUrls
import com.livechatinc.chatsdk.src.utils.Logger as InternalLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class KtorNetworkClient(private val json: Json, private val buildInfo: BuildInfo) :
    NetworkClient {
    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.ANDROID
            level = InternalLogger.getLogLevel().toKtorLogLevel
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpRequestRetry) {
            //TODO: specify retry policy
        }
        install(HttpCache)
    }

    override suspend fun fetchChatUrl(): String {
        return withContext(Dispatchers.IO) {
            val urls: ChatWidgetUrls = client.get(buildInfo.mobileConfigUrl).body()

            return@withContext urls.chatUrl!!
        }
    }
}

private val InternalLogger.LogLevel.toKtorLogLevel: LogLevel
    get() {
        return when (this) {
            InternalLogger.LogLevel.VERBOSE -> LogLevel.ALL
            InternalLogger.LogLevel.DEBUG -> LogLevel.ALL
            InternalLogger.LogLevel.INFO -> LogLevel.INFO
            InternalLogger.LogLevel.WARN -> LogLevel.NONE
            InternalLogger.LogLevel.ERROR -> LogLevel.NONE
            InternalLogger.LogLevel.NONE -> LogLevel.NONE
        }
    }
