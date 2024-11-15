package com.livechatinc.chatwidget.src

import com.livechatinc.chatwidget.src.models.ChatMessage

interface ChatWidgetCallbackListener {
    fun chatLoaded()
    fun hideChatWidget()
    fun onChatMessage(message: ChatMessage?)
    fun onError(cause: Throwable)
    fun onFileChooserActivityNotFound()
}
