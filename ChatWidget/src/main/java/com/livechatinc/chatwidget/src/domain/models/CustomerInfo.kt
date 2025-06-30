package com.livechatinc.chatwidget.src.domain.models

import kotlinx.serialization.Serializable

@Serializable
internal data class CustomerInfo(
    val name: String?,
    val email: String?,
    val customParams: Map<String, String>?
)
