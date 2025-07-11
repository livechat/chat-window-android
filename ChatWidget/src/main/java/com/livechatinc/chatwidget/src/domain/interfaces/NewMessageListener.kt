package com.livechatinc.chatwidget.src.domain.interfaces

import androidx.annotation.MainThread
import com.livechatinc.chatwidget.src.domain.models.ChatMessage

fun interface NewMessageListener {
    @MainThread
    fun onNewMessage(message: ChatMessage?)
}
