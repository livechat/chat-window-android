package com.livechatinc.chatsdk.src.utils

import kotlinx.serialization.json.Json

internal object JsonProvider {
    val instance: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
}
