package com.livechatinc.chatwidget.src

import com.livechatinc.chatwidget.src.models.ChatMessage

class LiveChatEventDispatcher {
    private val listeners = mutableSetOf<LiveChatViewCallbackListener>()

    fun addListener(listener: LiveChatViewCallbackListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: LiveChatViewCallbackListener) {
        listeners.remove(listener)
    }

    fun clear() {
        listeners.clear()
    }

    fun dispatchOnLoaded() {
//        listeners.forEach { it.onLoaded() }
    }

    fun dispatchOnHide() {
//        listeners.forEach { it.onHide() }
    }

    fun dispatchOnNewMessage(message: ChatMessage?) {
//        listeners.forEach { it.onNewMessage(message) }
    }

    fun dispatchOnError(cause: Throwable) {
//        listeners.forEach { it.onError(cause) }
    }

    fun dispatchOnFileChooserActivityNotFound() {
        listeners.forEach { it.onFileChooserActivityNotFound() }
    }
}
