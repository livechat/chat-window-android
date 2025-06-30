package com.livechatinc.chatwidget.src.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class MessageType {
    @SerialName("uiReady")
    UI_READY,

    @SerialName("hideChatWindow")
    HIDE_CHAT_WINDOW,

    @SerialName("newMessage")
    NEW_MESSAGE
}
