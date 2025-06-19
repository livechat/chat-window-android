package com.livechatinc.chatwidget.src.listeners

import android.net.Uri
import androidx.annotation.MainThread

interface UrlHandler {
    /**
     * Handle url clicks. Return true if the URL was handled, false otherwise.
     */
    @MainThread
    fun handleUrl(uri: Uri): Boolean
}
