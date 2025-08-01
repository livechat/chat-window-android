package com.livechatinc.chatsdk.src.domain.interfaces

import android.net.Uri
import android.webkit.ValueCallback
import com.livechatinc.chatsdk.src.domain.models.FilePickerMode

internal interface LiveChatViewInternal {
    fun loadUrl(url: String)

    fun startFilePicker(
        filePathCallback: ValueCallback<Array<Uri>>?,
        filePickerMode: FilePickerMode,
    )

    fun launchExternalBrowser(uri: Uri)

    fun postWebViewMessage(callback: String?, data: String)

    fun isChatShown(): Boolean
}
