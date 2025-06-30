package com.livechatinc.chatwidget.src.domain.interfaces

import android.net.Uri
import android.webkit.ValueCallback
import com.livechatinc.chatwidget.src.domain.models.FileChooserMode

internal interface LiveChatViewInternal {
    fun loadUrl(url: String)

    fun startFilePicker(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserMode: FileChooserMode,
    )

    fun launchExternalBrowser(uri: Uri)

    fun postWebViewMessage(callback: String?, data: String)
}
