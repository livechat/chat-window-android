package com.livechatinc.chatwidget.src.utils

import android.content.ActivityNotFoundException
import android.net.Uri
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.chatwidget.src.domain.presenters.LiveChatViewPresenter


internal class FileSharing(
    private val registry: ActivityResultRegistry,
    private val presenter: LiveChatViewPresenter
) : DefaultLifecycleObserver {
    private var getContent: ActivityResultLauncher<String>? = null
    private var getMultipleContent: ActivityResultLauncher<String>? = null

    private val filesUploadCallback: ValueCallback<Array<Uri>>?
        get() = LiveChat.getInstance().filesUploadCallback

    override fun onCreate(owner: LifecycleOwner) {
        registerSingleContentContract(owner)
        registerMultipleContentContract(owner)
    }

    private fun registerSingleContentContract(owner: LifecycleOwner) {
        getContent = registry.register(
            "liveChatFileResultRegistryKey",
            owner,
            ActivityResultContracts.GetContent()
        ) { file: Uri? ->
            filesUploadCallback?.onReceiveValue(
                if (file != null) arrayOf(file) else emptyArray()
            )
        }
    }

    private fun registerMultipleContentContract(owner: LifecycleOwner) {
        getMultipleContent = registry.register(
            "liveChatMultipleFilesResultRegistryKey",
            owner,
            ActivityResultContracts.GetMultipleContents()
        ) { value ->
            filesUploadCallback?.onReceiveValue(value.toTypedArray())
        }
    }

    fun selectFile(filePathCallback: ValueCallback<Array<Uri>>?) {
        LiveChat.getInstance().setFileUploadCallback(filePathCallback)

        try {
            getContent!!.launch("*/*")
        } catch (exception: ActivityNotFoundException) {
            presenter.onFileChooserActivityNotFound()
            filesUploadCallback?.onReceiveValue(emptyArray())
        }
    }

    fun selectFiles(filePathCallback: ValueCallback<Array<Uri>>?) {
        LiveChat.getInstance().setFileUploadCallback(filePathCallback)

        try {
            getMultipleContent!!.launch("*/*")
        } catch (exception: ActivityNotFoundException) {
            presenter.onFileChooserActivityNotFound()
            filesUploadCallback?.onReceiveValue(emptyArray())
        }
    }
}
