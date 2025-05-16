package com.livechatinc.chatwidget.src.common

import kotlinx.serialization.json.Json

object JsonProvider {
    val instance: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
}
