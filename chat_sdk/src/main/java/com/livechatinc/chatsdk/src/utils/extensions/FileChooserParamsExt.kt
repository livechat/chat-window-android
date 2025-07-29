package com.livechatinc.chatsdk.src.utils.extensions

import android.webkit.WebChromeClient.FileChooserParams
import com.livechatinc.chatsdk.src.domain.models.FilePickerMode

internal fun FileChooserParams?.filePickerMode(): FilePickerMode {
    val mode = this?.mode ?: FileChooserParams.MODE_OPEN

    return if (mode == FileChooserParams.MODE_OPEN_MULTIPLE) FilePickerMode.MULTIPLE else FilePickerMode.SINGLE
}
