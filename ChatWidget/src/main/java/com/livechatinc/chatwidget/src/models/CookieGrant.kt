package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.Serializable

@Serializable
data class CookieGrant(
    val cookies: List<Cookie>,
)

@Serializable
data class Cookie(
    val name: String,
    val value: String,
    val domain: String?,
    val path: String?,
    val expires: Long?,
    val maxAge: Int?,
    val secure: Boolean,
    val httpOnly: Boolean,
    val extensions: Map<String, String?>,
)
