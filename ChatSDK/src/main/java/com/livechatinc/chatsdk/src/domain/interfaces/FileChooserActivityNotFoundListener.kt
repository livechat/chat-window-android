package com.livechatinc.chatsdk.src.domain.interfaces

import androidx.annotation.MainThread

fun interface FileChooserActivityNotFoundListener {
    @MainThread
    fun onFileChooserActivityNotFound()
}
