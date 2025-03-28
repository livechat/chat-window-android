package com.livechatinc.chatwidget

import android.content.Context
import androidx.activity.ComponentActivity
import com.livechatinc.chatwidget.src.TokenManager
import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.common.ChatWidgetUtils
import com.livechatinc.chatwidget.src.components.LiveChatActivity
import com.livechatinc.chatwidget.src.data.core.KtorNetworkClient
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
import com.livechatinc.chatwidget.src.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.models.CookieGrant
import kotlinx.serialization.json.Json

class LiveChat : LiveChatInterface() {
    private val json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    private val buildInfo: BuildInfo = BuildInfo(
        apiHost = "https://cdn.livechatinc.com/",
        apiPath = "app/mobile/urls.json",
        accountsApiUrl = "https://accounts.livechat.com/v2/customer/token",
    )
    internal val networkClient: NetworkClient = KtorNetworkClient(json, buildInfo)
    private var tokenManager: TokenManager = TokenManager(networkClient) {
        identityCallback(it)
    }

    private var licence: String? = null
    private var applicationContext: Context? = null
    private var groupId: String = "0"
    private var customerName: String? = null
    private var customerEmail: String? = null
    private var customParams: Map<String, String>? = null

    // Custom Identity Provider
    private var licenceId: String? = null
    private var clientId: String? = null
    internal var identityCallback: (CookieGrant) -> Unit = { }
    private var identityGrant: CookieGrant? = null

    companion object {
        @Volatile
        private var instance: LiveChat? = null

        @JvmStatic
        fun getInstance(): LiveChat =
            instance ?: synchronized(this) {
                instance ?: LiveChat().also { instance = it }
            }

        @JvmStatic
        fun initialize(licence: String, context: Context) {
            getInstance().apply {
                this.licence = licence
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
        customerName = name
        customerEmail = email
        this.groupId = groupId ?: this.groupId
        this.customParams = customParams
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
        cookieGrantCallback: (CookieGrant) -> Unit,
    ) {
        this.licenceId = licenceId
        this.clientId = clientId
        identityCallback = cookieGrantCallback
    }

    override fun logInCustomer(cookieGrant: CookieGrant?) {
        identityGrant = cookieGrant
    }

    internal fun createChatConfiguration(): ChatWidgetConfig {
        return ChatWidgetConfig(
            requireNotNull(licence),
            groupId,
            customerName,
            customerEmail,
            customParams,
            clientId,
            licenceId,
            identityGrant,
        )
    }

    private fun startChatActivity(context: ComponentActivity) {
        LiveChatActivity.start(context)
    }

    internal fun hasToken(): Boolean {
        return tokenManager.hasToken()
    }

    internal suspend fun getToken(): ChatWidgetToken? {
        return tokenManager.getToken(createChatConfiguration())
    }

    internal suspend fun getFreshToken(): ChatWidgetToken? {
        return tokenManager.getFreshToken(createChatConfiguration())
    }
}
