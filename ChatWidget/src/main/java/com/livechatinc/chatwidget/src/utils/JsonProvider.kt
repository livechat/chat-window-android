package com.livechatinc.chatwidget.src.utils

import kotlinx.serialization.json.Json

object JsonProvider {
    val instance: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
}
