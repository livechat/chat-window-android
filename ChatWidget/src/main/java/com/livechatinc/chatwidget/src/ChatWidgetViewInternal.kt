package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import com.livechatinc.chatwidget.src.models.FileChooserMode

internal interface ChatWidgetViewInternal {
    fun launchExternalBrowser(uri: Uri)

    fun loadUrl(url: String)

    fun runOnUiThread(action: Runnable?)

    fun startFilePicker(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserMode: FileChooserMode,
    )
}
