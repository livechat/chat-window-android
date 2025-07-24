package com.livechatinc.chatsdk.src.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ChatWidgetUrls(
    @SerialName("chat_url") val chatUrl: String? = null
)
