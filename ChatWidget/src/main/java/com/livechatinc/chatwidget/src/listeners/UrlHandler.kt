package com.livechatinc.chatwidget.src.listeners

import android.net.Uri
import androidx.annotation.MainThread

interface UrlHandler {
    /**
     * Handle url clicks. To intercept the link and handle it on your side, return **true**
     */
    @MainThread
    fun handleUrl(uri: Uri): Boolean
}
