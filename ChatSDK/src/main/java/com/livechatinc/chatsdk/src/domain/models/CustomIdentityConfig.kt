package com.livechatinc.chatsdk.src.domain.models

import kotlinx.serialization.Serializable

@Serializable
internal data class CustomIdentityConfig(
    val licenseId: String,
    val clientId: String,
    val identityGrant: IdentityGrant?,
)
