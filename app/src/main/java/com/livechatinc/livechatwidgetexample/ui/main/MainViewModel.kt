package com.livechatinc.livechatwidgetexample.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.livechatinc.chatsdk.LiveChat
import com.livechatinc.chatsdk.src.domain.interfaces.ErrorListener
import com.livechatinc.chatsdk.src.domain.interfaces.FilePickerActivityNotFoundListener
import com.livechatinc.chatsdk.src.domain.interfaces.NewMessageListener
import com.livechatinc.chatsdk.src.presentation.LiveChatView
import com.livechatinc.livechatwidgetexample.data.SettingsRepository

class MainViewModel : ViewModel() {
    private val repository = SettingsRepository.getInstance()

    val settings = repository.data
    val messageCounter = MutableLiveData(0)
    val chatBubbleVisibility = MutableLiveData(false)

    val keepLiveChatViewInMemory: Boolean
        get() = settings.value?.keepLiveChatViewInMemory != false

    init {
        LiveChat.getInstance().getLiveChatView().init(initListener = object :
            LiveChatView.InitListener {
            override fun onUIReady() {
                chatBubbleVisibility.value = true
            }

            override fun onError(cause: Throwable) {
                chatBubbleVisibility.value = false
            }
        })
        LiveChat.getInstance().newMessageListener =
            NewMessageListener { _, isChatShown ->
                if (!isChatShown) {
                    messageCounter.value = (messageCounter.value ?: 0) + 1
                }
            }
        LiveChat.getInstance().filePickerNotFoundListener = FilePickerActivityNotFoundListener {
            Log.e("Example", "File picker not found. Please check your configuration.")
        }
        LiveChat.getInstance().errorListener = ErrorListener { error ->
            Log.e("Example", "Error occurred: ${error.message}")
        }
    }

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
        LiveChat.getInstance().newMessageListener = null
        LiveChat.getInstance().filePickerNotFoundListener = null
        LiveChat.getInstance().errorListener = null
        super.onCleared()
    }
}
