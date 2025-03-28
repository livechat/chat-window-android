package com.livechatinc.chatwidget.src

import androidx.annotation.MainThread
import com.livechatinc.chatwidget.src.models.ChatMessage

interface LiveChatViewCallbackListener {
    @MainThread
    fun onLoaded()

    @MainThread
    fun onHide()

    @MainThread
    fun onNewMessage(message: ChatMessage?)

    @MainThread
    fun onError(cause: Throwable)

    @MainThread
    fun onFileChooserActivityNotFound()
}
