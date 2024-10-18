package com.livechatinc.chatwidget.src

import com.google.gson.annotations.SerializedName

internal enum class MessageType {
    @SerializedName("uiReady")
    UI_READY,

    @SerializedName("hideChatWindow")
    HIDE_CHAT_WINDOW,

    @SerializedName("newMessage")
    TYPE_NEW_MESSAGE
}
