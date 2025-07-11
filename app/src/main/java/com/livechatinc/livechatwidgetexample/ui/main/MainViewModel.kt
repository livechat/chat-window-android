package com.livechatinc.livechatwidgetexample.ui.main

import androidx.lifecycle.ViewModel
import com.livechatinc.livechatwidgetexample.data.SettingsRepository

class MainViewModel : ViewModel() {
    private val repository = SettingsRepository.getInstance()

    val settings = repository.data

    fun updateSettings(
        customerName: String,
        customerEmail: String,
        groupId: String,
        customParams: Map<String, String>? = null
    ) {
        repository.updateSettings(customerName, customerEmail, groupId, customParams)
    }
}
