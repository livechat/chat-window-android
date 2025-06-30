package com.livechatinc.chatwidget.src.domain.models

internal data class CustomerTokenResponse(
    val token: ChatWidgetToken,
    val identityGrant: IdentityGrant,
)
