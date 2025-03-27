package com.livechatinc.chatwidget

import android.content.Context
import androidx.activity.ComponentActivity
import com.livechatinc.chatwidget.src.common.ChatWidgetUtils
import com.livechatinc.chatwidget.src.components.LiveChatActivity
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig

internal class LiveChat : LiveChatInterface() {

    private var licence: String? = null
    private var applicationContext: Context? = null
    private var groupId: String = "0"
    private var customerName: String? = null
    private var customerEmail: String? = null
    private var customParams: Map<String, String>? = null

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

    internal fun createChatConfiguration(): ChatWidgetConfig {
        return ChatWidgetConfig(
            requireNotNull(licence),
            groupId,
            customerName,
            customerEmail,
            customParams
        )
    }

    private fun startChatActivity(context: ComponentActivity) {
        LiveChatActivity.start(context)
    }

}
