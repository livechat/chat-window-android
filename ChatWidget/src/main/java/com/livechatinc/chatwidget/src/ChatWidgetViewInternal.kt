package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback

interface ChatWidgetViewInternal {
    fun loadUrl(url: String)

    fun launchExternalBrowser(uri: Uri)

    fun startFilePicker(filePathCallback: ValueCallback<Array<Uri>>?)
}
