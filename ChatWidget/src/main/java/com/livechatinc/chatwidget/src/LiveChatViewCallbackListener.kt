package com.livechatinc.chatwidget.src

import androidx.annotation.MainThread

interface LiveChatViewCallbackListener {
    @MainThread
    fun onFileChooserActivityNotFound()
}
