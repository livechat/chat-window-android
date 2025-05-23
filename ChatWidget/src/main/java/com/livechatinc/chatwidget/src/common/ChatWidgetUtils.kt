package com.livechatinc.chatwidget.src.common

import android.webkit.CookieManager
import android.webkit.WebStorage

object ChatWidgetUtils {
    /**
     * Clears cookies and web storage discarding user's chat session
     */
    fun clearSession() {
        WebStorage.getInstance().deleteAllData()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }
}
