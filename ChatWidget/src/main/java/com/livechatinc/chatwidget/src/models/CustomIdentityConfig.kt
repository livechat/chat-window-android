package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.Serializable

@Serializable
internal data class CustomIdentityConfig(
    val licenceId: String,
    val clientId: String,
    val identityGrant: IdentityGrant?,
)
