package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ChatWidgetUrls(
    @SerialName("chat_url") val chatUrl: String? = null
) {}
