package com.livechatinc.chatwidget.src

import androidx.annotation.MainThread

interface LiveChatViewInitCallbackListener {
    @MainThread
    fun onUIReady()

    @MainThread
    fun onHide()

    @MainThread
    fun onError(cause: Throwable)
}
