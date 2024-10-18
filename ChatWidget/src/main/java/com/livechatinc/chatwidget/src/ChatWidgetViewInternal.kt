package com.livechatinc.chatwidget.src

import android.net.Uri

interface ChatWidgetViewInternal {
    fun loadUrl(url: String)

    fun launchExternalBrowser(uri: Uri)
}
