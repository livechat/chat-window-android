package com.livechatinc.chatwidget.src

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


internal class ChatWindowLifecycleObserver(
    private val registry: ActivityResultRegistry,
    private val activityNotFoundCallback: ActivityNotFoundCallback
) : DefaultLifecycleObserver {
    private var getContent: ActivityResultLauncher<String>? = null
    private var getMultipleContent: ActivityResultLauncher<String>? = null
    private val resultLiveData = MutableLiveData<Array<Uri>>()

    override fun onCreate(owner: LifecycleOwner) {
        registerSingleContentContract(owner)
        registerMultipleContentContract(owner)
    }

    private fun registerSingleContentContract(owner: LifecycleOwner) {
        getContent = registry.register<String, Uri>(
            "chatWindowFileResultRegisterKey",
            owner,
            ActivityResultContracts.GetContent()
        ) { file: Uri? ->
            resultLiveData.postValue(
                if (file != null) arrayOf(file) else emptyArray()
            )
        }
    }

    private fun registerMultipleContentContract(owner: LifecycleOwner) {
        getMultipleContent = registry.register(
            "chatWindowMultipleFilesResultRegisterKey",
            owner,
            ActivityResultContracts.GetMultipleContents()
        ) { value -> resultLiveData.postValue(value.toTypedArray()) }
    }

    fun selectFile() {
        //TODO: consider using accept type from chrome client callback
        try {
            getContent!!.launch("*/*")
        } catch (exception: ActivityNotFoundException) {
            activityNotFoundCallback.onActivityNotFoundException()
            resultLiveData.postValue(emptyArray())
        }
    }

    fun selectFiles() {
        try {
            getMultipleContent!!.launch("*/*")
        } catch (exception: ActivityNotFoundException) {
            activityNotFoundCallback.onActivityNotFoundException()
            resultLiveData.postValue(emptyArray())
        }
    }

    fun getResultLiveData(): LiveData<Array<Uri>> {
        return resultLiveData
    }
}

internal fun interface ActivityNotFoundCallback {
    fun onActivityNotFoundException()
}
