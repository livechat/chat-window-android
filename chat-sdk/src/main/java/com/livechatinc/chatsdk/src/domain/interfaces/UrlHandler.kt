package com.livechatinc.chatsdk.src.domain.interfaces

import android.net.Uri
import androidx.annotation.MainThread

fun interface UrlHandler {
    /**
     * Handle url clicks. To intercept the link and handle it on your side, return **true**
     */
    @MainThread
    fun handleUrl(uri: Uri): Boolean
}
