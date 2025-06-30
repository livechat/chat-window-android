package com.livechatinc.chatwidget.src.domain.interfaces

import androidx.annotation.MainThread

interface ErrorListener {
    @MainThread
    fun onError(cause: Throwable)
}
