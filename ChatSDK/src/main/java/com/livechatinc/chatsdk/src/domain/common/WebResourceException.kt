package com.livechatinc.chatsdk.src.domain.common


class WebResourceException(
    errorCode: Int,
    description: String,
    failingUrl: String,
) :
    Exception("$errorCode : $failingUrl: $description")

