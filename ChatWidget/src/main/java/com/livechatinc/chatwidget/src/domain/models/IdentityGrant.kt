package com.livechatinc.chatwidget.src.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class IdentityGrant(
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
