package com.livechatinc.chatsdk.src.data.domain

internal interface NetworkClient {
    suspend fun fetchChatUrl(): String
}
