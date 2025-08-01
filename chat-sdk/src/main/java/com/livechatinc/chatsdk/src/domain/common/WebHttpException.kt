package com.livechatinc.chatsdk.src.domain.common

class WebHttpException(
    errorCode: Int,
    description: String,
    failingUrl: String,
) :
    Exception("$errorCode : $failingUrl: $description")
