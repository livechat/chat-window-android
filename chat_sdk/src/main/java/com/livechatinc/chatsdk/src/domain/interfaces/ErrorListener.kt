package com.livechatinc.chatsdk.src.domain.interfaces

import androidx.annotation.MainThread

fun interface ErrorListener {
    @MainThread
    fun onError(cause: Throwable)
}
