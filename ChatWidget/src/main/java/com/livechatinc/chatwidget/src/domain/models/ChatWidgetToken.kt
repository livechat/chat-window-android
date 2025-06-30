package com.livechatinc.chatwidget.src.domain.models

import kotlinx.serialization.Serializable

@Serializable
internal data class ChatWidgetToken(
    var accessToken: String? = null,
    var entityId: String? = null,
    var expiresIn: Int? = null,
    var licenseId: String? = null,
    var tokenType: String? = null,
    var creationDate: Long? = null,
)

