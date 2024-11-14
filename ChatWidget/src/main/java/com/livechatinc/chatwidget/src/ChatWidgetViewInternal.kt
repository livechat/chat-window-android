package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback

interface ChatWidgetViewInternal {
    fun launchExternalBrowser(uri: Uri)

    fun loadUrl(url: String)

    fun runOnUiThread(action: Runnable?)

    fun startFilePicker(filePathCallback: ValueCallback<Array<Uri>>?)
}
