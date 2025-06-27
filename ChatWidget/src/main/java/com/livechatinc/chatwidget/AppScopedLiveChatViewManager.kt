package com.livechatinc.chatwidget

interface AppScopedLiveChatViewManager {
    fun getLiveChatView(): LiveChatView

    fun destroyLiveChatView()
}
