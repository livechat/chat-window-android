package com.livechatinc.chatwidget.src.domain.common


class WebResourceException(
    errorCode: Int,
    description: String,
    failingUrl: String,
) :
    Exception("$errorCode : $failingUrl: $description")

