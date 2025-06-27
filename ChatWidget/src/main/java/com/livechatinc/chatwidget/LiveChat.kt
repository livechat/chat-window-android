package com.livechatinc.chatwidget

import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import com.livechatinc.chatwidget.src.listeners.FileChooserActivityNotFoundListener
import com.livechatinc.chatwidget.src.AppScopedLiveChatViewManager
import com.livechatinc.chatwidget.src.listeners.NewMessageListener
import com.livechatinc.chatwidget.src.TokenManager
import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.common.ChatWidgetUtils
import com.livechatinc.chatwidget.src.common.JsonProvider
import com.livechatinc.chatwidget.src.components.LiveChatActivity
import com.livechatinc.chatwidget.src.data.core.KtorNetworkClient
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.listeners.UrlHandler
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

    private val viewManager: AppScopedLiveChatViewManager by lazy {
        AppScopedLiveChatViewManager(applicationContext)
    }

    private var license: String? = null
    private lateinit var applicationContext: Context
    private var groupId: String? = null

    private var customerInfo: CustomerInfo? = null

    // Custom Identity Provider
    private var licenseId: String? = null
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

    internal var urlHandler: UrlHandler? = null
    fun setUrlHandler(handler: UrlHandler?) {
        urlHandler = handler
    }

    internal var filesUploadCallback: ValueCallback<Array<Uri>>? = null
    internal fun setFileUploadCallback(filePathCallback: ValueCallback<Array<Uri>>?) {
        filesUploadCallback = filePathCallback
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
            license: String,
            context: Context,
            lifecycleScope: LiveChatViewLifecycleScope? = null
        ) {
            getInstance().apply {
                this.license = license
                this.applicationContext = context.applicationContext
                this.liveChatViewLifecycleScope =
                    lifecycleScope ?: LiveChatViewLifecycleScope.APP
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
        requireNotNull(license) { "SDK not initialized. Call initialize() first" }

        startChatActivity(context)
    }

    override suspend fun signOutCustomer() {
        //TODO: destroy view if its KEEP_ALIVE
        ChatWidgetUtils.clearSession()
    }

    fun getLiveChatView(): LiveChatView {
        return viewManager.getLiveChatView()
    }

    fun destroyLiveChatView() {
        viewManager.destroyLiveChatView()
    }

    override fun configureIdentityProvider(
        licenseId: String,
        clientId: String,
        onIdentityGrantChange: (IdentityGrant) -> Unit,
    ) {
        this.licenseId = licenseId
        this.clientId = clientId
        identityCallback = onIdentityGrantChange
    }

    override fun logInCustomer(identityGrant: IdentityGrant?) {
        //TODO: consider having identityGrant a suspend callback
        this.identityGrant = identityGrant
    }

    internal fun createLiveChatConfig(): LiveChatConfig {
        return LiveChatConfig(
            license = requireNotNull(license),
            groupId = groupId ?: LiveChatConfig.DEFAULT_GROUP_ID,
            customerInfo = customerInfo,
            customIdentityConfig = createIdentityProvider(),
        )
    }

    private fun createIdentityProvider(): CustomIdentityConfig? {
        if (licenseId.isNullOrEmpty() || clientId.isNullOrEmpty()) {
            return null
        }

        return CustomIdentityConfig(
            licenseId = requireNotNull(licenseId),
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
}
