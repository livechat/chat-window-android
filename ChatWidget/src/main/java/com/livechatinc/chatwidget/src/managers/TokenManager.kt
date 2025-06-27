package com.livechatinc.chatwidget.src.managers

import com.livechatinc.chatwidget.src.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.models.LiveChatConfig

internal interface TokenManager {
    fun hasToken(): Boolean

    suspend fun getToken(config: LiveChatConfig): ChatWidgetToken?

    suspend fun getFreshToken(config: LiveChatConfig): ChatWidgetToken?
}
