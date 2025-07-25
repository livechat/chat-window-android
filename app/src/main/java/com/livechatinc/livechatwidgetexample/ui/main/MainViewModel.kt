package com.livechatinc.livechatwidgetexample.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.livechatinc.chatsdk.LiveChat
import com.livechatinc.livechatwidgetexample.data.SettingsRepository

class MainViewModel : ViewModel() {
    private val repository = SettingsRepository.getInstance()

    init {
        LiveChat.getInstance().setNewMessageListener { _, isChatShown ->
            if (!isChatShown) {
                messageCounter.value = (messageCounter.value ?: 0) + 1
            }
        }
    }

    val settings = repository.data
    val messageCounter = MutableLiveData(0)

    val keepLiveChatViewInMemory: Boolean
        get() = settings.value?.keepLiveChatViewInMemory != false

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

    override fun onCleared() {
        LiveChat.getInstance().setNewMessageListener(null)
        super.onCleared()
    }
}
