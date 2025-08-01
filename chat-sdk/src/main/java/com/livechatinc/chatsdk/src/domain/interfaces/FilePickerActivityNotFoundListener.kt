package com.livechatinc.chatsdk.src.domain.interfaces

import androidx.annotation.MainThread

fun interface FilePickerActivityNotFoundListener {
    @MainThread
    fun onFilePickerActivityNotFound()
}
