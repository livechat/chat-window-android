package com.livechatinc.livechatwidgetexample.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.chatwidget.src.domain.models.ChatMessage
import com.livechatinc.livechatwidgetexample.data.SettingsRepository

class MainViewModel : ViewModel() {

    init {
        LiveChat.getInstance().setNewMessageListener { message: ChatMessage? ->
            messageCounter.value = (messageCounter.value ?: 0) + 1
        }
    }

    private val repository = SettingsRepository.getInstance()

    val settings = repository.data
    val messageCounter = MutableLiveData(0)

    fun updateSettings(
        customerName: String,
        customerEmail: String,
        groupId: String,
        customParams: Map<String, String>? = null
    ) {
        repository.updateSettings(customerName, customerEmail, groupId, customParams)
    }

    fun onShowChat() {
        messageCounter.value = 0
    }
}
