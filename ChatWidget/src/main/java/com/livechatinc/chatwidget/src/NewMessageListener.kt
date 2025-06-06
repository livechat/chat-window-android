package com.livechatinc.chatwidget.src

import androidx.annotation.MainThread
import com.livechatinc.chatwidget.src.models.ChatMessage

interface NewMessageListener {
    @MainThread
    fun onNewMessage(message: ChatMessage?)
}
