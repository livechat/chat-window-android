package com.livechatinc.chatwidget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ValueCallback
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.livechatinc.chatwidget.src.LiveChatViewCallbackListener
import com.livechatinc.chatwidget.src.ChatWidgetChromeClient
import com.livechatinc.chatwidget.src.ChatWidgetJSBridge
import com.livechatinc.chatwidget.src.ChatWidgetPresenter
import com.livechatinc.chatwidget.src.ChatWidgetViewInternal
import com.livechatinc.chatwidget.src.ChatWidgetWebViewClient
import com.livechatinc.chatwidget.src.FileSharing
import com.livechatinc.chatwidget.src.common.BuildInfo
import com.livechatinc.chatwidget.src.data.core.KtorNetworkClient
import com.livechatinc.chatwidget.src.data.domain.NetworkClient
import com.livechatinc.chatwidget.src.extensions.getActivity
import com.livechatinc.chatwidget.src.models.FileChooserMode
import kotlinx.serialization.json.Json

@SuppressLint("SetJavaScriptEnabled")
class LiveChatView(
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
        prettyPrint = true
    }

    private val buildInfo: BuildInfo = BuildInfo(
        apiHost = "https://cdn.livechatinc.com/",
        apiPath = "app/mobile/urls.json",
        accountsApiUrl = "https://accounts.livechat.com/v2/customer/token",
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
        //TODO: test set need for initial focus
        //webSettings.setNeedInitialFocus(false)

        //TODO: check need for dom storage
        webSettings.domStorageEnabled = true
        //TODO: investigate message sounds without user gesture
        webSettings.mediaPlaybackRequiresUserGesture = false

        // TODO: Check if clearCache interrupts with resuming the session
        // Seems to be needed for CIP callbacks
        webView.clearCache(true)

        webView.webChromeClient = ChatWidgetChromeClient(presenter)
        webView.webViewClient = ChatWidgetWebViewClient(presenter)

        webView.addJavascriptInterface(
            ChatWidgetJSBridge(presenter),
            ChatWidgetJSBridge.INTERFACE_NAME,
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

    fun init(callbackListener: LiveChatViewCallbackListener? = null) {
        presenter.setCallbackListener(callbackListener)

        val config = LiveChat.getInstance().createChatConfiguration()
        presenter.init(config)
    }

    override fun loadUrl(url: String) {
        webView.loadUrl(url)
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

    override fun launchExternalBrowser(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)

        context.startActivity(intent)
    }

    override fun postWebViewMessage(callback: String?, data: String) {
        println("### --> post message: $callback, $data")
        webView.post {
            webView.evaluateJavascript("javascript:$callback($data)", null)
        }
    }

    //TODO: no need for shared prefs?
    override fun saveTokenToPreferences(token: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("widgetToken", token)
        editor.apply()
    }

    override fun readTokenFromPreferences(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("widgetToken", null)

        return token
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
