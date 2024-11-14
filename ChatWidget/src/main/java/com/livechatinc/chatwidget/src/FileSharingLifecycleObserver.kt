package com.livechatinc.chatwidget.src

import android.content.ActivityNotFoundException
import android.net.Uri
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


internal class FileSharingLifecycleObserver(
    private val registry: ActivityResultRegistry,
    private val activityNotFoundCallback: ActivityNotFoundCallback
) : DefaultLifecycleObserver {
    private var getContent: ActivityResultLauncher<String>? = null
    private var getMultipleContent: ActivityResultLauncher<String>? = null
    private var uriArrayUploadCallback: ValueCallback<Array<Uri>>? = null

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
            uriArrayUploadCallback?.onReceiveValue(
                if (file != null) arrayOf(file) else emptyArray()
            )
        }
    }

    private fun registerMultipleContentContract(owner: LifecycleOwner) {
        getMultipleContent = registry.register(
            "chatWindowMultipleFilesResultRegisterKey",
            owner,
            ActivityResultContracts.GetMultipleContents()
        ) { value -> uriArrayUploadCallback?.onReceiveValue(value.toTypedArray()) }
    }

    fun selectFile(filePathCallback: ValueCallback<Array<Uri>>?) {
        //TODO: consider using accept type from chrome client callback
        uriArrayUploadCallback = filePathCallback

        try {
            getContent!!.launch("*/*")
        } catch (exception: ActivityNotFoundException) {
            activityNotFoundCallback.onActivityNotFoundException()
            uriArrayUploadCallback?.onReceiveValue(emptyArray())
        }
    }

    fun selectFiles(filePathCallback: ValueCallback<Array<Uri>>?) {
        uriArrayUploadCallback = filePathCallback

        try {
            getMultipleContent!!.launch("*/*")
        } catch (exception: ActivityNotFoundException) {
            activityNotFoundCallback.onActivityNotFoundException()
            uriArrayUploadCallback?.onReceiveValue(emptyArray())
        }
    }
}

internal fun interface ActivityNotFoundCallback {
    fun onActivityNotFoundException()
}
