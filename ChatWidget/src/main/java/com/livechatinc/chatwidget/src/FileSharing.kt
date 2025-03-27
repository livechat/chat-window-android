package com.livechatinc.chatwidget.src

import android.content.ActivityNotFoundException
import android.net.Uri
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


internal class FileSharing(
    private val registry: ActivityResultRegistry,
    private val presenter: ChatWidgetPresenter
) : DefaultLifecycleObserver {
    private var getContent: ActivityResultLauncher<String>? = null
    private var getMultipleContent: ActivityResultLauncher<String>? = null
    private var filesUploadCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(owner: LifecycleOwner) {
        registerSingleContentContract(owner)
        registerMultipleContentContract(owner)
    }

    private fun registerSingleContentContract(owner: LifecycleOwner) {
        getContent = registry.register(
            "chatWidgetFileResultRegistryKey",
            owner,
            ActivityResultContracts.GetContent()
        ) { file: Uri? ->
            filesUploadCallback?.onReceiveValue(
                if (file != null) arrayOf(file) else emptyArray()
            )
        }
    }

    private fun registerMultipleContentContract(owner: LifecycleOwner) {
        //TODO: what if there are two apps on user device with ChatWidget? Should it contain bundle id?
        getMultipleContent = registry.register(
            "chatWidgetMultipleFilesResultRegistryKey",
            owner,
            ActivityResultContracts.GetMultipleContents()
        ) { value -> filesUploadCallback?.onReceiveValue(value.toTypedArray()) }
    }

    fun selectFile(filePathCallback: ValueCallback<Array<Uri>>?) {
        //TODO: consider using accept type from chrome client callback
        filesUploadCallback = filePathCallback

        try {
            getContent!!.launch("*/*")
        } catch (exception: ActivityNotFoundException) {
            presenter.onFileChooserActivityNotFound()
            filesUploadCallback?.onReceiveValue(emptyArray())
        }
    }

    fun selectFiles(filePathCallback: ValueCallback<Array<Uri>>?) {
        filesUploadCallback = filePathCallback

        try {
            getMultipleContent!!.launch("*/*")
        } catch (exception: ActivityNotFoundException) {
            presenter.onFileChooserActivityNotFound()
            filesUploadCallback?.onReceiveValue(emptyArray())
        }
    }
}
