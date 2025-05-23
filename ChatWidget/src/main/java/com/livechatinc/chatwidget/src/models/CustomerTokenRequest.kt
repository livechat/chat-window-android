package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CustomerTokenRequest(
    @SerialName("grant_type") val grantType: String? = null,
    @SerialName("response_type") val responseType: String? = null,
    @SerialName("client_id") val clientId: String? = null,
    @SerialName("organization_id") val licenceId: String? = null,
)
