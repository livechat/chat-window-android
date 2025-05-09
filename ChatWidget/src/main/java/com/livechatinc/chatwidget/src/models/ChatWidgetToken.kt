package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class ChatWidgetToken(
    @SerialName("accessToken") var accessToken: String? = null,
    @SerialName("entityId") var entityId: String? = null,
    @SerialName("expiresIn") var expiresIn: Int? = null,
    @SerialName("licenseId") var licenseId: String? = null,
    @SerialName("tokenType") var tokenType: String? = null,
    @SerialName("creationDate") var creationDate: Long? = null,
)

