package com.livechatinc.chatwidget.src.domain.interfaces

import androidx.annotation.MainThread

fun interface FileChooserActivityNotFoundListener {
    @MainThread
    fun onFileChooserActivityNotFound()
}
