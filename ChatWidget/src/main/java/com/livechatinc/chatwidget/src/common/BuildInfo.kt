package com.livechatinc.chatwidget.src.common

data class BuildInfo(
    val mobileConfigHost: String,
    val mobileConfigPath: String,
    val accountsApiUrl: String,
    val accountsRestoreCustomerApiUrl: String,
){
    val mobileConfigUrl: String
        get() = "$mobileConfigHost$mobileConfigPath"
}
