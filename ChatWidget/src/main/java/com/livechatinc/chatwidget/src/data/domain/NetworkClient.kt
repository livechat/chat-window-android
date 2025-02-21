package com.livechatinc.chatwidget.src.data.domain

import com.livechatinc.chatwidget.src.models.ChatWidgetToken

interface NetworkClient {
    suspend fun fetchChatUrl(): String

    suspend fun getVisitorToken(
        license: String,
        licenceId: String,
        clientId: String,
    ): ChatWidgetToken
}
