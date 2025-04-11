package com.livechatinc.chatwidget.src

import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.LiveChatConfig
import com.livechatinc.chatwidget.src.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.models.IdentityGrant
import com.livechatinc.chatwidget.src.models.CustomerTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class TokenManager(
    private val networkClient: NetworkClient,
    private val identityCallback: (IdentityGrant) -> Unit = { }
) {
    private var currentToken: ChatWidgetToken? = null

    fun hasToken(): Boolean = currentToken != null

    suspend fun getToken(config: LiveChatConfig): ChatWidgetToken? =
        currentToken ?: getFreshToken(config)

    suspend fun getFreshToken(config: LiveChatConfig): ChatWidgetToken? {
        if (!config.isCustomIdentityEnabled) return null

        return withContext(Dispatchers.IO) {
            val response = fetchVisitorToken(config)
            currentToken = response.token

            identityCallback(response.identityGrant)

            response.token
        }
    }

    private suspend fun fetchVisitorToken(config: LiveChatConfig): CustomerTokenResponse {
        val identityConfig = config.customIdentityConfig!!

        return networkClient.getVisitorToken(
            config.license,
            identityConfig.licenceId,
            identityConfig.clientId,
            identityConfig.identityGrant,
        )
    }
}
