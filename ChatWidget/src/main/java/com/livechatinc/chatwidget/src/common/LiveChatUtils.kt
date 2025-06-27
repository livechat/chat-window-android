package com.livechatinc.chatwidget.src.common

import android.webkit.CookieManager
import android.webkit.WebStorage

internal object LiveChatUtils {
    fun clearSession() {
        WebStorage.getInstance().deleteAllData()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }
}
