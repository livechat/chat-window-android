package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ChatWidgetUrls(
    @SerialName("chat_url") val chatUrl: String? = null
)