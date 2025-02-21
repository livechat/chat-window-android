package com.livechatinc.chatwidget.src.data.domain

import com.livechatinc.chatwidget.src.models.CustomerTokenResponse
import io.ktor.http.Cookie

interface NetworkClient {
    suspend fun fetchChatUrl(): String

    suspend fun getVisitorToken(
        license: String,
        licenceId: String,
        clientId: String,
        lcCookies: List<Cookie>? = null,
    ): CustomerTokenResponse
}
