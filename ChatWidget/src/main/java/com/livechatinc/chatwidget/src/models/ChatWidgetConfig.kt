package com.livechatinc.chatwidget.src.models

data class ChatWidgetConfig @JvmOverloads constructor(
    val license: String,
    val group: String = "0",
    val visitorName: String? = null,
    val visitorEmail: String? = null,
    val customParameters: Map<String, String> = emptyMap(),
)
