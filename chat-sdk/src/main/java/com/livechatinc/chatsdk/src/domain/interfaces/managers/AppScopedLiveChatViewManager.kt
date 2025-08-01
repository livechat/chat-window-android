package com.livechatinc.chatsdk.src.domain.interfaces.managers

import com.livechatinc.chatsdk.src.presentation.LiveChatView

interface AppScopedLiveChatViewManager {
    fun getLiveChatView(): LiveChatView

    fun destroyLiveChatView()
}
