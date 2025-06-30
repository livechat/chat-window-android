package com.livechatinc.chatwidget.src.domain.interfaces.managers

import com.livechatinc.chatwidget.src.domain.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.domain.models.LiveChatConfig

internal interface TokenManager {
    fun hasToken(): Boolean

    suspend fun getToken(config: LiveChatConfig): ChatWidgetToken?

    suspend fun getFreshToken(config: LiveChatConfig): ChatWidgetToken?
}
