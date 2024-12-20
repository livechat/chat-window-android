package com.livechatinc.chatwidget.src.common

import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebStorage

class ChatWidgetUtils {
    /**
     * Clears cookies and web storage discarding user's chat session
     */
    fun clearSession() {
        WebStorage.getInstance().deleteAllData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.removeSessionCookies(null)
            cookieManager.flush()
        }
    }
}
