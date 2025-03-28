package com.livechatinc.chatwidget.src.data.domain

import com.livechatinc.chatwidget.src.models.IdentityGrant
import com.livechatinc.chatwidget.src.models.CustomerTokenResponse

interface NetworkClient {
    suspend fun fetchChatUrl(): String

    suspend fun getVisitorToken(
        license: String,
        licenceId: String,
        clientId: String,
        identityGrant: IdentityGrant? = null,
    ): CustomerTokenResponse
}
