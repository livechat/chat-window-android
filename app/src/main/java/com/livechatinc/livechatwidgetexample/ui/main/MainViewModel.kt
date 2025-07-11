package com.livechatinc.livechatwidgetexample.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.livechatwidgetexample.data.SettingsRepository

class MainViewModel : ViewModel() {

    init {
        LiveChat.getInstance().setNewMessageListener { _ ->
            messageCounter.value = (messageCounter.value ?: 0) + 1
        }
    }

    private val repository = SettingsRepository.getInstance()

    val settings = repository.data
    val messageCounter = MutableLiveData(0)

    fun updateCustomerInfo(
        groupId: String,
        customerName: String,
        customerEmail: String,
        customParams: Map<String, String>? = null
    ) {
        repository.updateCustomerInfo(customerName, customerEmail, groupId, customParams)
    }

    fun updateLifecycleScopeMode(enabled: Boolean) {
        repository.updateLifecycleScopeMode(enabled)
    }

    fun onShowChat() {
        messageCounter.value = 0
    }
}
