package com.livechatinc.chatwidget.src.data.domain

import com.livechatinc.chatwidget.src.models.CookieGrant
import com.livechatinc.chatwidget.src.models.CustomerTokenResponse

interface NetworkClient {
    suspend fun fetchChatUrl(): String

    suspend fun getVisitorToken(
        license: String,
        licenceId: String,
        clientId: String,
        cookieGrant: CookieGrant? = null,
    ): CustomerTokenResponse
}
