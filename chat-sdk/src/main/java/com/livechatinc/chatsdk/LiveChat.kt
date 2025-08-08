package com.livechatinc.chatsdk

import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import androidx.annotation.VisibleForTesting
import com.livechatinc.chatsdk.src.core.LiveChatViewLifecycleScope
import com.livechatinc.chatsdk.src.domain.interfaces.managers.AppScopedLiveChatViewManager
import com.livechatinc.chatsdk.src.domain.interfaces.FilePickerActivityNotFoundListener
import com.livechatinc.chatsdk.src.core.managers.AppScopedLiveChatViewManagerImpl
import com.livechatinc.chatsdk.src.domain.interfaces.managers.SessionManager
import com.livechatinc.chatsdk.src.domain.models.BuildInfo
import com.livechatinc.chatsdk.src.utils.JsonProvider
import com.livechatinc.chatsdk.src.presentation.LiveChatActivity
import com.livechatinc.chatsdk.src.data.core.KtorNetworkClient
import com.livechatinc.chatsdk.src.data.domain.NetworkClient
import com.livechatinc.chatsdk.src.domain.interfaces.ErrorListener
import com.livechatinc.chatsdk.src.domain.interfaces.UrlHandler
import com.livechatinc.chatsdk.src.core.managers.SessionManagerImpl
import com.livechatinc.chatsdk.src.domain.interfaces.NewMessageListener
import com.livechatinc.chatsdk.src.domain.models.LiveChatConfig
import com.livechatinc.chatsdk.src.domain.models.CustomerInfo
import com.livechatinc.chatsdk.src.presentation.LiveChatView

class LiveChat private constructor(
    private val license: String,
    internal val networkClient: NetworkClient,
    private val viewManagerProvider: (() -> AppScopedLiveChatViewManager),
    private val sessionManager: SessionManager = SessionManagerImpl()
) {
    private val viewManager: AppScopedLiveChatViewManager by lazy {
        viewManagerProvider.invoke()
    }

    private var groupId: String? = null
    private var customerInfo: CustomerInfo? = null

    internal var errorListener: ErrorListener? = null
    fun setErrorListener(listener: ErrorListener?) {
        errorListener = listener
    }

    private var _newMessageListener: NewMessageListener? = null

    internal val messageListener: NewMessageListener?
        get() = _newMessageListener

    fun setNewMessageListener(listener: NewMessageListener?) {
        _newMessageListener = listener
    }

    internal var filePickerNotFoundListener: FilePickerActivityNotFoundListener? =
        null

    fun setFilePickerNotFoundListener(listener: FilePickerActivityNotFoundListener?) {
        filePickerNotFoundListener = listener
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
                    networkClient = KtorNetworkClient(
                        JsonProvider.instance,
                        BuildInfo(
                            mobileConfigHost = "https://cdn.livechatinc.com/",
                            mobileConfigPath = "app/mobile/urls.json",
                        ),
                        context,
                    )
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
            viewManager: AppScopedLiveChatViewManager,
            sessionManager: SessionManager,
        ): LiveChat {
            return LiveChat(
                license,
                networkClient,
                { viewManager },
                sessionManager,
            )
        }
    }

    fun setCustomerInfo(
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

    fun show(context: Context) {
        startChatActivity(context)
    }

    /**
     * Clears cookies and web storage discarding user's chat session
     * Removes [LiveChatView] when created in [LiveChatViewLifecycleScope.APP] scope
     */
    fun signOutCustomer() {
        sessionManager.clearSession()
        destroyLiveChatView()
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

    internal fun createLiveChatConfig(): LiveChatConfig {
        return LiveChatConfig(
            license = requireNotNull(license),
            groupId = groupId ?: LiveChatConfig.DEFAULT_GROUP_ID,
            customerInfo = customerInfo,
        )
    }

    private fun startChatActivity(context: Context) {
        LiveChatActivity.start(context)
    }
}
