package com.livechatinc.chatwidget

import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import androidx.annotation.VisibleForTesting
import com.livechatinc.chatwidget.src.core.LiveChatViewLifecycleScope
import com.livechatinc.chatwidget.src.domain.interfaces.managers.AppScopedLiveChatViewManager
import com.livechatinc.chatwidget.src.domain.interfaces.FileChooserActivityNotFoundListener
import com.livechatinc.chatwidget.src.core.managers.AppScopedLiveChatViewManagerImpl
import com.livechatinc.chatwidget.src.domain.interfaces.managers.SessionManager
import com.livechatinc.chatwidget.src.domain.interfaces.managers.TokenManager
import com.livechatinc.chatwidget.src.core.managers.TokenManagerImpl
import com.livechatinc.chatwidget.src.domain.models.BuildInfo
import com.livechatinc.chatwidget.src.utils.JsonProvider
import com.livechatinc.chatwidget.src.presentation.LiveChatActivity
import com.livechatinc.chatwidget.src.data.core.KtorNetworkClient
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.domain.interfaces.ErrorListener
import com.livechatinc.chatwidget.src.domain.interfaces.UrlHandler
import com.livechatinc.chatwidget.src.core.managers.SessionManagerImpl
import com.livechatinc.chatwidget.src.domain.interfaces.NewMessageListener
import com.livechatinc.chatwidget.src.domain.models.LiveChatConfig
import com.livechatinc.chatwidget.src.domain.models.ChatWidgetToken
import com.livechatinc.chatwidget.src.domain.models.CustomerInfo
import com.livechatinc.chatwidget.src.domain.models.IdentityGrant
import com.livechatinc.chatwidget.src.domain.models.CustomIdentityConfig
import com.livechatinc.chatwidget.src.presentation.LiveChatView

class LiveChat private constructor(
    private val license: String,
    internal val networkClient: NetworkClient = KtorNetworkClient(
        JsonProvider.instance,
        BuildInfo(
            mobileConfigHost = "https://cdn.livechatinc.com/",
            mobileConfigPath = "app/mobile/urls.json",
            accountsApiUrl = "https://accounts.livechat.com/v2/customer/token",
        ),
    ),
    private val tokenManagerProvider: (() -> TokenManager)? = null,
    private val viewManagerProvider: (() -> AppScopedLiveChatViewManager),
    private val sessionManager: SessionManager = SessionManagerImpl()
) : LiveChatInterface() {
    private val tokenManager: TokenManager by lazy {
        tokenManagerProvider?.invoke() ?: TokenManagerImpl(networkClient) {
            identityCallback(it)
        }
    }

    private val viewManager: AppScopedLiveChatViewManager by lazy {
        viewManagerProvider.invoke()
    }

    private var groupId: String? = null

    private var customerInfo: CustomerInfo? = null

    // Custom Identity Provider
    private var licenseId: String? = null
    private var clientId: String? = null
    private var identityGrant: IdentityGrant? = null
    internal var identityCallback: (IdentityGrant) -> Unit = { }

    internal var errorListener: ErrorListener? = null
    fun setErrorListener(listener: ErrorListener?) {
        errorListener = listener
    }

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
            instance ?: throw IllegalStateException("SDK not initialized. Call initialize() first")


        @JvmStatic
        @JvmOverloads
        fun initialize(
            license: String,
            context: Context,
            lifecycleScope: LiveChatViewLifecycleScope? = null
        ) {
            require(license.isNotBlank()) { "License cannot be empty" }

            synchronized(this) {
                instance = LiveChat(
                    license = license,
                    viewManagerProvider = { AppScopedLiveChatViewManagerImpl(context) },
                ).apply {
                    this.liveChatViewLifecycleScope =
                        lifecycleScope ?: LiveChatViewLifecycleScope.APP
                }
            }
        }

        @VisibleForTesting
        internal fun createForTesting(
            license: String,
            networkClient: NetworkClient,
            tokenManager: TokenManager,
            viewManager: AppScopedLiveChatViewManager,
            sessionManager: SessionManager,
        ): LiveChat {
            return LiveChat(
                license,
                networkClient,
                { tokenManager },
                { viewManager },
                sessionManager,
            )
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
        startChatActivity(context)
    }

    /**
     * Clears cookies and web storage discarding user's chat session
     * Removes [LiveChatView] when created in [LiveChatViewLifecycleScope.APP] scope
     */
    override fun signOutCustomer() {
        destroyLiveChatView()
        sessionManager.clearSession()
    }

    /**
     * Should be used with [LiveChatViewLifecycleScope.APP] scope
     * Creates [LiveChatView] instance or returns existing one
     * */
    fun getLiveChatView(): LiveChatView {
        return viewManager.getLiveChatView()
    }

    /**
     * Should be used with [LiveChatViewLifecycleScope.APP] scope
     * Removes [LiveChatView] from parent and destroys it
     * */
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
