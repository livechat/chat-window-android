package com.livechatinc.chatwidget

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.livechatinc.chatwidget.src.ChatWidgetCallbackListener
import com.livechatinc.chatwidget.src.ChatWidgetChromeClient
import com.livechatinc.chatwidget.src.ChatWidgetJSBridge
import com.livechatinc.chatwidget.src.ChatWidgetPresenter
import com.livechatinc.chatwidget.src.ChatWidgetViewInternal
import com.livechatinc.chatwidget.src.ChatWidgetWebViewClient
import com.livechatinc.chatwidget.src.ChatWindowLifecycleObserver
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig

@SuppressLint("SetJavaScriptEnabled")
class ChatWidget(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs), ChatWidgetViewInternal, DefaultLifecycleObserver {
    private lateinit var uriObserver: Observer<Array<Uri>>
    private lateinit var observer: ChatWindowLifecycleObserver
    private var webView: WebView
    private var presenter: ChatWidgetPresenter
    private var uriArrayUploadCallback: ValueCallback<Array<Uri>>? = null

    init {
        inflate(context, R.layout.chat_widget_internal, this)
        webView = findViewById(R.id.chat_widget_webview)

        presenter = ChatWidgetPresenter(this)

        configureWebView()

        supportFileSharing()
    }

    private fun configureWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        //TODO: check need for dom storage
        webSettings.domStorageEnabled = true
        //TODO: investigate message sounds without user gesture
        webSettings.mediaPlaybackRequiresUserGesture = false

        webView.webChromeClient = ChatWidgetChromeClient(this)
        webView.webViewClient = ChatWidgetWebViewClient()

        webView.addJavascriptInterface(
            ChatWidgetJSBridge(presenter),
            ChatWidgetJSBridge.INTERFACE_NAME
        )
    }

    private fun supportFileSharing() {
        //TODO: check fragment, and regular activity
        //TODO: consider setting by the lib user
        getActivity().let { activity ->
            if (activity != null) {
                observer = ChatWindowLifecycleObserver(
                    activity.activityResultRegistry
                ) {
                    //TODO: handle activity not found
                }
                activity.lifecycle.addObserver(observer)
                uriObserver =
                    Observer { selectedFiles: Array<Uri> -> this.onFileChooserResult(selectedFiles) }
                observer.getResultLiveData().observe(activity, uriObserver)
            }
        }
    }

    private fun onFileChooserResult(selectedFiles: Array<Uri>) {
        uriArrayUploadCallback?.onReceiveValue(selectedFiles)
        uriArrayUploadCallback = null
    }

    fun init(config: ChatWidgetConfig) {
        presenter.init(config)
    }

    fun setCallbackListener(callbackListener: ChatWidgetCallbackListener) {
        presenter.setCallbackListener(callbackListener)
    }

    override fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    override fun launchExternalBrowser(uri: Uri) {
        TODO("Not yet implemented")
    }

    override fun startFilePicker(filePathCallback: ValueCallback<Array<Uri>>?) {
        uriArrayUploadCallback = filePathCallback
        observer.selectFile()
    }

    fun runOnUiThread(action: Runnable?) {
        post(action)
    }

    override fun onDetachedFromWindow() {
        webView.apply {
            onPause()
            pauseTimers()
            destroy()
        }

        super.onDetachedFromWindow()
    }

    override fun onResume(owner: LifecycleOwner) {
        //TODO: pass lifecycle events to the webView
        super.onResume(owner)
        println("### onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        println("### onPause")
    }
}

fun View.getActivity(): AppCompatActivity? {
    var context = this.context

    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }

        context = context.baseContext
    }

    return null
}
