package com.livechatinc.chatwidget.src.data.domain

import com.livechatinc.chatwidget.src.domain.models.IdentityGrant
import com.livechatinc.chatwidget.src.domain.models.CustomerTokenResponse

internal interface NetworkClient {
    suspend fun fetchChatUrl(): String

    suspend fun getCustomerToken(
        license: String,
        licenseId: String,
        clientId: String,
        identityGrant: IdentityGrant? = null,
    ): Result<CustomerTokenResponse>
}
