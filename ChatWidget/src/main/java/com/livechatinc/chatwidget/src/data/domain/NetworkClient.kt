package com.livechatinc.chatwidget.src.data.domain

import com.livechatinc.chatwidget.src.models.IdentityGrant
import com.livechatinc.chatwidget.src.models.CustomerTokenResponse

internal interface NetworkClient {
    suspend fun fetchChatUrl(): String

    suspend fun getCustomerToken(
        license: String,
        licenseId: String,
        clientId: String,
        identityGrant: IdentityGrant? = null,
    ): Result<CustomerTokenResponse>
}
