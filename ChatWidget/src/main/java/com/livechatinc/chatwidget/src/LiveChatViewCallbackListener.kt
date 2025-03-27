package com.livechatinc.chatwidget.src

import com.livechatinc.chatwidget.src.models.ChatMessage

interface LiveChatViewCallbackListener {
    fun onLoaded()
    fun onHide()
    fun onNewMessage(message: ChatMessage?)
    fun onError(cause: Throwable)
    fun onFileChooserActivityNotFound()
}
