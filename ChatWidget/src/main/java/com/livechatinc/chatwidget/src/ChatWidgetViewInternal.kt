package com.livechatinc.chatwidget.src

import android.net.Uri
import android.webkit.ValueCallback
import com.livechatinc.chatwidget.src.models.FileChooserMode

internal interface ChatWidgetViewInternal {
    fun loadUrl(url: String)

    fun runOnUiThread(action: Runnable?)

    fun startFilePicker(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserMode: FileChooserMode,
    )

    fun launchExternalBrowser(uri: Uri)

    fun postWebViewMessage(callback: String?, data: String)

    fun saveTokenToPreferences(token: String)

    fun readTokenFromPreferences(): String?
}
