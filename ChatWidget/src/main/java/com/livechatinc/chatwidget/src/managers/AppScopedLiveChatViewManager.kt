package com.livechatinc.chatwidget.src.managers

import com.livechatinc.chatwidget.LiveChatView

interface AppScopedLiveChatViewManager {
    fun getLiveChatView(): LiveChatView

    fun destroyLiveChatView()
}
