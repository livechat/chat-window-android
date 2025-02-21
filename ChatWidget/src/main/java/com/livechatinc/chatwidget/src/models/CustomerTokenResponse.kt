package com.livechatinc.chatwidget.src.models

data class CustomerTokenResponse(
    val token: ChatWidgetToken,
    val cookieGrant: CookieGrant,
)
