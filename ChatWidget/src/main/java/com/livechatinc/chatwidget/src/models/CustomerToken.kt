package com.livechatinc.chatwidget.src.models

import io.ktor.http.Cookie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerToken(
    @SerialName("access_token") var accessToken: String? = null,
    @SerialName("entity_id") var entityId: String? = null,
    @SerialName("expires_in") var expiresIn: Int? = null,
    @SerialName("organization_id") var organizationId: String? = null,
    @SerialName("token_type") var tokenType: String? = null

)

fun CustomerToken.toChatWidgetToken(
    license: String,
): ChatWidgetToken {
    return ChatWidgetToken(
        accessToken = accessToken,
        entityId = entityId,
        expiresIn = expiresIn?.times(1000),
        tokenType = tokenType,
        creationDate = System.currentTimeMillis(),
        licenseId = license,
    )
}


data class CustomerTokenResponse(
    val token: ChatWidgetToken,
    val cookieGrant: CookieGrant,
)

@Serializable
data class CookieGrant(
    val cookies: List<Cookie>,
)
