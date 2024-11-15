package com.livechatinc.chatwidget.src.data.domain

interface NetworkClient {
    suspend fun fetchChatUrl(): String
}
