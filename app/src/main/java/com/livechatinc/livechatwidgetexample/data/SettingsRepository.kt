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
) {
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
