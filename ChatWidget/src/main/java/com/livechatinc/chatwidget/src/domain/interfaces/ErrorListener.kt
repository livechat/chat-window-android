package com.livechatinc.chatwidget.src.domain.interfaces

import androidx.annotation.MainThread

fun interface ErrorListener {
    @MainThread
    fun onError(cause: Throwable)
}
