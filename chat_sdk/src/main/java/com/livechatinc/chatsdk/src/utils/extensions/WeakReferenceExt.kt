package com.livechatinc.chatsdk.src.utils.extensions

import java.lang.ref.WeakReference

internal fun <T> T?.toWeakReferenceOrNull(): WeakReference<T>? {
    return if (this != null) {
        WeakReference(this)
    } else {
        null
    }
}
