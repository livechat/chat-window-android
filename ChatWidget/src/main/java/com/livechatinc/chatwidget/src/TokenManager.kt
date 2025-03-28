package com.livechatinc.chatwidget.src

import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
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

    suspend fun getToken(config: ChatWidgetConfig): ChatWidgetToken? =
        currentToken ?: getFreshToken(config)

    suspend fun getFreshToken(config: ChatWidgetConfig): ChatWidgetToken? {
        if (!config.isCIPEnabled) return null

        return withContext(Dispatchers.IO) {
            val response = fetchVisitorToken(config)
            currentToken = response.token

            identityCallback(response.identityGrant)

            response.token
        }
    }

    private suspend fun fetchVisitorToken(config: ChatWidgetConfig): CustomerTokenResponse {
        return networkClient.getVisitorToken(
            config.license,
            config.licenceId!!,
            config.clientId!!,
            config.identityGrant,
        )
    }
}
