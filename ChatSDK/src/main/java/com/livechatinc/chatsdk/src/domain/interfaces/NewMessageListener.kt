package com.livechatinc.chatsdk.src.domain.interfaces

import androidx.annotation.MainThread
import com.livechatinc.chatsdk.src.domain.models.ChatMessage

fun interface NewMessageListener {
    @MainThread
    fun onNewMessage(message: ChatMessage?, isChatShown: Boolean)
}
