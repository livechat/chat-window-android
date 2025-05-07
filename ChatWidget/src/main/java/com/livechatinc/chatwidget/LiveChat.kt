package com.livechatinc.chatwidget

import android.content.Context
import androidx.activity.ComponentActivity
import com.livechatinc.chatwidget.src.TokenManager
import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.common.ChatWidgetUtils
import com.livechatinc.chatwidget.src.components.LiveChatActivity
import com.livechatinc.chatwidget.src.data.core.KtorNetworkClient
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.LiveChatConfig
import com.livechatinc.chatwidget.src.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.models.CustomerInfo
import com.livechatinc.chatwidget.src.models.IdentityGrant
import com.livechatinc.chatwidget.src.models.CustomIdentityConfig
import kotlinx.serialization.json.Json

class LiveChat : LiveChatInterface() {
    private val json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    private val buildInfo: BuildInfo = BuildInfo(
        mobileConfigHost = "https://cdn.livechatinc.com/",
        mobileConfigPath = "app/mobile/urls.json",
        accountsApiUrl = "https://accounts.livechat.com/v2/customer/token",
    )
    internal val networkClient: NetworkClient = KtorNetworkClient(json, buildInfo)
    private var tokenManager: TokenManager = TokenManager(networkClient) {
        identityCallback(it)
    }

    private var licence: String? = null
    private var applicationContext: Context? = null
    private var groupId: String? = null

    private var customerInfo: CustomerInfo? = null

    // Custom Identity Provider
    private var licenceId: String? = null
    private var clientId: String? = null
    private var identityGrant: IdentityGrant? = null
    internal var identityCallback: (IdentityGrant) -> Unit = { }

    companion object {
        @Volatile
        private var instance: LiveChat? = null

        @JvmStatic
        fun getInstance(): LiveChat =
            instance ?: synchronized(this) {
                instance ?: LiveChat().also { instance = it }
            }

        @JvmStatic
        @JvmOverloads
        fun initialize(licence: String, context: Context, groupId: String? = null) {
            getInstance().apply {
                this.licence = licence
                this.groupId = groupId
                this.applicationContext = context.applicationContext
            }
        }
    }

    override fun setCustomerInfo(
        name: String?,
        email: String?,
        groupId: String?,
        customParams: Map<String, String>?
    ) {
        customerInfo = CustomerInfo(
            name = name,
            email = email,
            customParams = customParams
        )
        this.groupId = groupId
    }

    override fun show(context: Context) {
        requireNotNull(licence) { "SDK not initialized. Call initialize() first" }

        if (context !is ComponentActivity) {
            throw IllegalArgumentException("Context must be ComponentActivity")
        }

        startChatActivity(context)
    }

    override suspend fun signOutCustomer() {
        ChatWidgetUtils.clearSession()
    }

    override fun configureIdentityProvider(
        licenceId: String,
        clientId: String,
        onIdentityGrantChange: (IdentityGrant) -> Unit,
    ) {
        this.licenceId = licenceId
        this.clientId = clientId
        identityCallback = onIdentityGrantChange
    }

    override fun logInCustomer(identityGrant: IdentityGrant?) {
        //TODO: consider having identityGrant a suspend callback
        this.identityGrant = identityGrant
    }

    internal fun createLiveChatConfig(): LiveChatConfig {
        return LiveChatConfig(
            license = requireNotNull(licence),
            groupId = groupId ?: LiveChatConfig.DEFAULT_GROUP_ID,
            customerInfo = customerInfo,
            customIdentityConfig = createIdentityProvider(),
        )
    }

    private fun createIdentityProvider(): CustomIdentityConfig? {
        if (licenceId.isNullOrEmpty() || clientId.isNullOrEmpty()) {
            return null
        }

        return CustomIdentityConfig(
            licenceId = requireNotNull(licenceId),
            clientId = requireNotNull(clientId),
            identityGrant = identityGrant,
        )
    }

    private fun startChatActivity(context: ComponentActivity) {
        LiveChatActivity.start(context)
    }

    internal fun hasToken(): Boolean {
        return tokenManager.hasToken()
    }

    internal suspend fun getToken(): ChatWidgetToken? {
        return tokenManager.getToken(createLiveChatConfig())
    }

    internal suspend fun getFreshToken(): ChatWidgetToken? {
        return tokenManager.getFreshToken(createLiveChatConfig())
    }
}
