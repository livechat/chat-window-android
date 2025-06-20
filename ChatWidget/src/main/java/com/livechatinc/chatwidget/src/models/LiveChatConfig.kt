package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.Serializable

@Serializable
internal data class LiveChatConfig @JvmOverloads constructor(
    val license: String,
    val groupId: String = DEFAULT_GROUP_ID,
    val customerInfo: CustomerInfo? = null,
    val customIdentityConfig: CustomIdentityConfig? = null
) {
    val isCustomIdentityEnabled: Boolean
        get() = customIdentityConfig != null &&
                customIdentityConfig.licenseId.isNotEmpty() &&
                customIdentityConfig.clientId.isNotEmpty()

    internal companion object {
        const val DEFAULT_GROUP_ID = "0"
    }
}
