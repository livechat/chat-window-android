package com.livechatinc.chatwidget.src.domain.interfaces.managers

import com.livechatinc.chatwidget.src.presentation.LiveChatView

interface AppScopedLiveChatViewManager {
    fun getLiveChatView(): LiveChatView

    fun destroyLiveChatView()
}
