package com.livechatinc.chatwidget.src.models

import kotlinx.serialization.Serializable

@Serializable
internal data class LiveChatConfig @JvmOverloads constructor(
    val license: String,
    val groupId: String = DEFAULT_GROUP_ID,
    val customerInfo: CustomerInfo? = null,
    val customIdentityConfig: CustomIdentityConfig? = null
) {
    constructor(
        license: String,
        groupId: String?,
        customerInfo: CustomerInfo? = null,
        customIdentityConfig: CustomIdentityConfig? = null
    ) : this(
        license = license,
        groupId = groupId ?: DEFAULT_GROUP_ID,
        customerInfo = customerInfo,
        customIdentityConfig = customIdentityConfig
    )

    val isCustomIdentityEnabled: Boolean
        get() = customIdentityConfig != null &&
                customIdentityConfig.licenceId.isNotEmpty() &&
                customIdentityConfig.clientId.isNotEmpty()

    private companion object {
        const val DEFAULT_GROUP_ID = "0"
    }
}
