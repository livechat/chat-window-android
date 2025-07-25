package com.livechatinc.livechatwidgetexample.data

import androidx.lifecycle.MutableLiveData
import com.livechatinc.chatsdk.LiveChat

class SettingsRepository {
    val data = MutableLiveData<CurrentSettings>()

    fun updateCustomerInfo(
        customerName: String,
        customerEmail: String,
        groupId: String,
        customParams: Map<String, String>? = null,
    ) {
        val currentSettings = data.value ?: CurrentSettings()

        data.value = currentSettings.copy(
            customerName = customerName,
            customerEmail = customerEmail,
            groupId = groupId,
            customParams = customParams,
        )

        LiveChat.getInstance().setCustomerInfo(customerName, customerEmail, groupId, customParams)
        LiveChat.getInstance().destroyLiveChatView()
    }

    fun updateLifecycleScopeMode(enabled: Boolean) {
        val currentSettings = data.value ?: CurrentSettings()
        data.value = currentSettings.copy(keepLiveChatViewInMemory = enabled)
    }

    fun updateIdentitySettings(licenseId: String?, clientId: String?) {
        val currentSettings = data.value ?: CurrentSettings()
        val identitySettings = currentSettings.identitySettings ?: IdentitySettings()

        data.value = currentSettings.copy(
            identitySettings = identitySettings.copy(
                licenseId = licenseId,
                clientId = clientId,
            ),
        )
    }

    companion object {
        @Volatile
        private var instance: SettingsRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SettingsRepository().also { instance = it }
            }
    }
}

data class CurrentSettings(
    val customerName: String? = null,
    val customerEmail: String? = null,
    val groupId: String = "0",
    val customParams: Map<String, String>? = null,
    val keepLiveChatViewInMemory: Boolean = true,
    val identitySettings: IdentitySettings? = null
) {
    val hasIdentityRelatedIds: Boolean
        get() = identitySettings?.let {
            !it.clientId.isNullOrEmpty() && !it.licenseId.isNullOrEmpty()
        } ?: false

    override fun toString(): String {
        return "CurrentSettings(\n" +
                "customerEmail=$customerEmail,\n" +
                "customerName=$customerName,\n" +
                "groupId='$groupId',\n" +
                "customParams=$customParams\n" +
                "keepLiveChatViewInMemory= $keepLiveChatViewInMemory\n" +
                ")"
    }
}

data class IdentitySettings(
    val licenseId: String? = null,
    val clientId: String? = null,
    val identityGrant: String? = null,
) {
    val configIds: String?
        get() {
            return if (!licenseId.isNullOrEmpty() && !clientId.isNullOrEmpty()) {
                "License ID: $licenseId,\nClient ID: $clientId"
            } else {
                null
            }
        }

    override fun toString(): String {
        return "IdentitySettings(\n" +
                "licenseId=$licenseId,\n" +
                "clientId=$clientId,\n" +
                "identityGrant=$identityGrant\n" +
                ")"
    }
}
