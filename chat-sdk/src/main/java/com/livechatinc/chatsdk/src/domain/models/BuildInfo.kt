package com.livechatinc.chatsdk.src.domain.models

data class BuildInfo(
    val mobileConfigHost: String,
    val mobileConfigPath: String,
){
    val mobileConfigUrl: String
        get() = "$mobileConfigHost$mobileConfigPath"
}
