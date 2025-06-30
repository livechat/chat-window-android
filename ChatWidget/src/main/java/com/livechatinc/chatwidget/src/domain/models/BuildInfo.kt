package com.livechatinc.chatwidget.src.domain.models

data class BuildInfo(
    val mobileConfigHost: String,
    val mobileConfigPath: String,
    val accountsApiUrl: String,
){
    val mobileConfigUrl: String
        get() = "$mobileConfigHost$mobileConfigPath"
}
