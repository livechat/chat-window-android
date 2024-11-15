package com.livechatinc.chatwidget.src.data.core

import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.ChatWidgetUrls
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class KtorNetworkClient(json: Json, val buildInfo: BuildInfo) : NetworkClient {
    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    override suspend fun fetchChatUrl(): String {
        return withContext(Dispatchers.IO) {
            val urls: ChatWidgetUrls = client.get("${buildInfo.apiHost}${buildInfo.apiPath}").body()

            return@withContext urls.chatUrl!!
        }
    }
}
