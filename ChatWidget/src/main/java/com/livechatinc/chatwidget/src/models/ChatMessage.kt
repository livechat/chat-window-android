package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val text: String? = null,
    val id: String? = null,
    val timestamp: String? = null,
    val author: Author? = null,
) {
    override fun toString(): String {
        return "ChatMessage(\n" +
                "text=$text,\n" +
                "id=$id,\n" +
                "timestamp=$timestamp,\n" +
                "author=$author\n" +
                ")"
    }
}
