package com.livechatinc.chatwidget.src.common

class WebHttpException(
    errorCode: Int,
    description: String,
    failingUrl: String,
) :
    Exception("$errorCode : $failingUrl: $description")
