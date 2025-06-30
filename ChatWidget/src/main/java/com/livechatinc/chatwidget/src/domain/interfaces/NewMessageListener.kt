package com.livechatinc.chatwidget.src.domain.interfaces

import androidx.annotation.MainThread
import com.livechatinc.chatwidget.src.domain.models.ChatMessage

interface NewMessageListener {
    @MainThread
    fun onNewMessage(message: ChatMessage?)
}
