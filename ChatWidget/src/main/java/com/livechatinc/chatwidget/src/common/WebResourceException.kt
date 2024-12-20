package com.livechatinc.chatwidget.src.common


class WebResourceException(
    errorCode: Int,
    description: String,
    failingUrl: String,
) :
    Exception("$errorCode : $failingUrl: $description")

