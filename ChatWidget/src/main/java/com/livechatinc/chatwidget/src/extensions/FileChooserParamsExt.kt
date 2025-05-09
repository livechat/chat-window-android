package com.livechatinc.chatwidget.src.extensions

import android.webkit.WebChromeClient.FileChooserParams
import com.livechatinc.chatwidget.src.models.FileChooserMode

internal fun FileChooserParams?.fileChooserMode(): FileChooserMode {
    val mode = this?.mode ?: FileChooserParams.MODE_OPEN

    return if (mode == FileChooserParams.MODE_OPEN_MULTIPLE) FileChooserMode.MULTIPLE else FileChooserMode.SINGLE
}
