package com.livechatinc.chatwidget.src.managers

import com.livechatinc.chatwidget.src.common.Logger
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.LiveChatConfig
import com.livechatinc.chatwidget.src.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.models.IdentityGrant
import com.livechatinc.chatwidget.src.models.CustomerTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class TokenManagerImpl(
    private val networkClient: NetworkClient,
    private val identityCallback: (IdentityGrant) -> Unit = { }
) : TokenManager {
    private var currentToken: ChatWidgetToken? = null

    override fun hasToken(): Boolean = currentToken != null

    override suspend fun getToken(config: LiveChatConfig): ChatWidgetToken? =
        currentToken ?: getFreshToken(config)

    override suspend fun getFreshToken(config: LiveChatConfig): ChatWidgetToken? {
        if (!config.isCustomIdentityEnabled) return null

        return withContext(Dispatchers.IO) {
            val result = fetchCustomerToken(config)

            if (result.isSuccess) {
                val response = result.getOrNull()!!
                currentToken = response.token

                identityCallback(response.identityGrant)

                result.getOrNull()?.token
            } else {
                Logger.e("Obtaining user identity failed", throwable = result.exceptionOrNull())

                return@withContext null
            }
        }
    }

    private suspend fun fetchCustomerToken(config: LiveChatConfig): Result<CustomerTokenResponse> {
        val identityConfig = config.customIdentityConfig!!

        return networkClient.getCustomerToken(
            config.license,
            identityConfig.licenseId,
            identityConfig.clientId,
            identityConfig.identityGrant,
        )
    }
}
