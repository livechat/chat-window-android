package com.livechatinc.chatwidget.src

interface ChatWidgetCallbackListener {
    fun chatLoaded()
    fun hideChatWidget()
    fun onChatMessage(message: String)
}
