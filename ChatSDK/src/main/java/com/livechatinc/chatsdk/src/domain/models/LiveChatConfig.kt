package com.livechatinc.chatsdk.src.domain.models

import kotlinx.serialization.Serializable

@Serializable
internal data class LiveChatConfig @JvmOverloads constructor(
    val license: String,
    val groupId: String = DEFAULT_GROUP_ID,
    val customerInfo: CustomerInfo? = null,
) {
    internal companion object {
        const val DEFAULT_GROUP_ID = "0"
    }
}
