package com.livechatinc.livechatwidgetexample.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.livechatinc.chatsdk.LiveChat
import com.livechatinc.chatsdk.src.domain.models.IdentityGrant
import com.livechatinc.livechatwidgetexample.BuildConfig
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

    fun checkIdentityProviderConfig() {
        repository.updateIdentitySettings(
            licenseId = BuildConfig.LICENSE_ID,
            clientId = BuildConfig.CLIENT_ID,
        )
    }

    fun onSetIdentityGrant(identityGrant: IdentityGrant?) {
        LiveChat.getInstance().configureIdentityProvider(
            settings.value!!.identitySettings!!.licenseId!!,
            settings.value!!.identitySettings!!.clientId!!,
        ) { _ -> //Handle callback
        }
        LiveChat.getInstance().logInCustomer(identityGrant)
        LiveChat.getInstance().destroyLiveChatView()
    }
}
