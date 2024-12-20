package com.livechatinc.chatwidget

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ValueCallback
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.livechatinc.chatwidget.src.ChatWidgetCallbackListener
import com.livechatinc.chatwidget.src.ChatWidgetChromeClient
import com.livechatinc.chatwidget.src.ChatWidgetJSBridge
import com.livechatinc.chatwidget.src.ChatWidgetPresenter
import com.livechatinc.chatwidget.src.ChatWidgetViewInternal
import com.livechatinc.chatwidget.src.ChatWidgetWebViewClient
import com.livechatinc.chatwidget.src.FileSharing
import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.data.core.KtorNetworkClient
import com.livechatinc.chatwidget.src.data.core.RetrofitNetworkClient
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.extensions.getActivity
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig
import com.livechatinc.chatwidget.src.models.FileChooserMode
import kotlinx.serialization.json.Json

@SuppressLint("SetJavaScriptEnabled")
class ChatWidget(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs), ChatWidgetViewInternal, DefaultLifecycleObserver {
    private var fileSharing: FileSharing? = null
    private var webView: WebView
    private var presenter: ChatWidgetPresenter
    private val networkClient: NetworkClient
    private val json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    private val buildInfo: BuildInfo = BuildInfo(
        apiHost = "https://cdn.livechatinc.com/",
        apiPath = "app/mobile/urls.json",
    )

    init {
        inflate(context, R.layout.chat_widget_internal, this)
        webView = findViewById(R.id.chat_widget_webview)
        networkClient = KtorNetworkClient(json, buildInfo)
        presenter = ChatWidgetPresenter(this, networkClient)

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

        webView.webChromeClient = ChatWidgetChromeClient(presenter)
        webView.webViewClient = ChatWidgetWebViewClient(presenter)

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
                fileSharing = FileSharing(
                    activity.activityResultRegistry,
                    presenter,
                )
                activity.lifecycle.addObserver(fileSharing!!)
            }
        }
    }

    fun init(config: ChatWidgetConfig) {
        presenter.init(config)
    }

    fun setCallbackListener(callbackListener: ChatWidgetCallbackListener) {
        presenter.setCallbackListener(callbackListener)
    }

    override fun launchExternalBrowser(uri: Uri) {
        TODO("Not yet implemented")
    }

    override fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    override fun runOnUiThread(action: Runnable?) {
        post(action)
    }

    override fun startFilePicker(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserMode: FileChooserMode,
    ) {
        //TODO: handle case where wasn't possible to support file sharing
        when (fileChooserMode) {
            FileChooserMode.SINGLE -> fileSharing?.selectFile(filePathCallback)
            FileChooserMode.MULTIPLE -> fileSharing?.selectFiles(filePathCallback)
        }
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
