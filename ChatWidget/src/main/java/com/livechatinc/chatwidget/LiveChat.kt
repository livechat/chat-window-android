package com.livechatinc.chatwidget

import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import com.livechatinc.chatwidget.src.listeners.FileChooserActivityNotFoundListener
import com.livechatinc.chatwidget.src.LiveChatViewManager
import com.livechatinc.chatwidget.src.listeners.NewMessageListener
import com.livechatinc.chatwidget.src.TokenManager
import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.common.ChatWidgetUtils
import com.livechatinc.chatwidget.src.common.JsonProvider
import com.livechatinc.chatwidget.src.components.LiveChatActivity
import com.livechatinc.chatwidget.src.data.core.KtorNetworkClient
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.models.LiveChatConfig
import com.livechatinc.chatwidget.src.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.models.CustomerInfo
import com.livechatinc.chatwidget.src.models.IdentityGrant
import com.livechatinc.chatwidget.src.models.CustomIdentityConfig

class LiveChat : LiveChatInterface() {
    private val buildInfo: BuildInfo = BuildInfo(
        mobileConfigHost = "https://cdn.livechatinc.com/",
        mobileConfigPath = "app/mobile/urls.json",
        accountsApiUrl = "https://accounts.livechat.com/v2/customer/token",
    )
    internal val networkClient: NetworkClient = KtorNetworkClient(JsonProvider.instance, buildInfo)
    private var tokenManager: TokenManager = TokenManager(networkClient) {
        identityCallback(it)
    }

    private val viewManager: LiveChatViewManager by lazy {
        LiveChatViewManager(applicationContext)
    }

    private var licence: String? = null
    private lateinit var applicationContext: Context
    private var groupId: String? = null

    private var customerInfo: CustomerInfo? = null

    // Custom Identity Provider
    private var licenceId: String? = null
    private var clientId: String? = null
    private var identityGrant: IdentityGrant? = null
    internal var identityCallback: (IdentityGrant) -> Unit = { }

    internal var newMessageListener: NewMessageListener? = null
    fun setNewMessageListener(listener: NewMessageListener?) {
        newMessageListener = listener
    }

    internal var fileChooserNotFoundListener: FileChooserActivityNotFoundListener? = null
    fun setNewMessageListener(listener: FileChooserActivityNotFoundListener?) {
        fileChooserNotFoundListener = listener
    }

    internal lateinit var liveChatViewLifecycleScope: LiveChatViewLifecycleScope

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
        fun initialize(
            licence: String,
            context: Context,
            groupId: String? = null,
            lifecycleScope: LiveChatViewLifecycleScope? = null
        ) {
            getInstance().apply {
                this.licence = licence
                this.groupId = groupId
                this.applicationContext = context.applicationContext
                this.liveChatViewLifecycleScope =
                    lifecycleScope ?: LiveChatViewLifecycleScope.KEEP_ALIVE
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

        startChatActivity(context)
    }

    override suspend fun signOutCustomer() {
        //TODO: destroy view if its KEEP_ALIVE
        ChatWidgetUtils.clearSession()
    }

    internal fun getLiveChatView(): LiveChatView {
        return viewManager.getLiveChatView()
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

    private fun startChatActivity(context: Context) {
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

    internal var filesUploadCallback: ValueCallback<Array<Uri>>? = null

    internal fun setFileUploadCallback(filePathCallback: ValueCallback<Array<Uri>>?) {
        filesUploadCallback = filePathCallback
    }
}
