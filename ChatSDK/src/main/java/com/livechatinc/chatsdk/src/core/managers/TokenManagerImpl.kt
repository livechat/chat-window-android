package com.livechatinc.chatsdk.src.core.managers

import com.livechatinc.chatsdk.src.utils.Logger
import com.livechatinc.chatsdk.src.data.domain.NetworkClient
import com.livechatinc.chatsdk.src.domain.models.LiveChatConfig
import com.livechatinc.chatsdk.src.domain.models.ChatWidgetToken
import com.livechatinc.chatsdk.src.domain.models.IdentityGrant
import com.livechatinc.chatsdk.src.domain.models.CustomerTokenResponse
import com.livechatinc.chatsdk.src.domain.interfaces.managers.TokenManager
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
