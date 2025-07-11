package com.livechatinc.livechatwidgetexample.data

import androidx.lifecycle.MutableLiveData
import com.livechatinc.chatwidget.LiveChat

class SettingsRepository {
    val data = MutableLiveData<CurrentSettings>()

    fun updateSettings(
        customerName: String,
        customerEmail: String,
        groupId: String,
        customParams: Map<String, String>? = null
    ) {
        val newSettings = CurrentSettings(
            customerName = customerName,
            customerEmail = customerEmail,
            groupId = groupId,
            customParams = customParams
        )
        data.value = newSettings

        LiveChat.getInstance().setCustomerInfo(customerName, customerEmail, groupId, customParams)
        LiveChat.getInstance().destroyLiveChatView()
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
    val customerName: String?,
    val customerEmail: String?,
    val groupId: String = "0",
    val customParams: Map<String, String>? = null
)
