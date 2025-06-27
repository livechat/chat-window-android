package com.livechatinc.chatwidget.src.listeners

import androidx.annotation.MainThread

interface ErrorListener {
    @MainThread
    fun onError(cause: Throwable)
}
