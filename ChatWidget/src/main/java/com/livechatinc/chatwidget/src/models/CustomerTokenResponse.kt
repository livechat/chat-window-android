package com.livechatinc.chatwidget.src.models

internal data class CustomerTokenResponse(
    val token: ChatWidgetToken,
    val identityGrant: IdentityGrant,
)
