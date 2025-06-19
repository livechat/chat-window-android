package com.livechatinc.chatwidget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.livechatinc.chatwidget.src.ChatWidgetChromeClient
import com.livechatinc.chatwidget.src.ChatWidgetJSBridge
import com.livechatinc.chatwidget.src.ChatWidgetPresenter
import com.livechatinc.chatwidget.src.ChatWidgetViewInternal
import com.livechatinc.chatwidget.src.ChatWidgetWebViewClient
import com.livechatinc.chatwidget.src.FileSharing
import com.livechatinc.chatwidget.src.listeners.LiveChatViewInitListener
import com.livechatinc.chatwidget.src.common.Logger
import com.livechatinc.chatwidget.src.extensions.getActivity
import com.livechatinc.chatwidget.src.models.FileChooserMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
class LiveChatView(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs), ChatWidgetViewInternal, DefaultLifecycleObserver {
    private var fileSharing: FileSharing? = null
    private var webView: WebView
    private var presenter: ChatWidgetPresenter

    init {
        Logger.d("### LiveChatView constructor")
        inflate(context, R.layout.live_chat_widget_internal, this)
        webView = findViewById(R.id.live_chat_webview)
        presenter = ChatWidgetPresenter(this, LiveChat.getInstance().networkClient)

        configureWebView()
    }

    fun isUIReady(): Boolean {
        return presenter.uiReady
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
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        // TODO: Check if clearCache interrupts with resuming the session
        // Seems to be needed for CIP callbacks
//        webView.clearCache(true)

        webView.webChromeClient = ChatWidgetChromeClient(presenter)
        webView.webViewClient = ChatWidgetWebViewClient(presenter)

        webView.addJavascriptInterface(
            ChatWidgetJSBridge(presenter),
            ChatWidgetJSBridge.INTERFACE_NAME,
        )
    }

    fun supportFileSharing(activity: AppCompatActivity) {
        fileSharing = FileSharing(
            activity.activityResultRegistry,
            presenter,
        )
        activity.lifecycle.addObserver(fileSharing!!)
        activity.lifecycle.addObserver(this)
    }

    fun init(callbackListener: LiveChatViewInitListener? = null) {
        if (callbackListener != null) {
            presenter.setInitListener(callbackListener)
        }

        val config = LiveChat.getInstance().createLiveChatConfig()
        presenter.init(config)
    }

    override fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    override fun startFilePicker(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserMode: FileChooserMode,
    ) {
        if (fileSharing == null) {
            Logger.e("File sharing is not set up. Call supportFileSharing() first.")

            return
        }

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
        Logger.d("### --> post message: $callback, $data")
        CoroutineScope(Dispatchers.Main).launch {
            webView.evaluateJavascript("javascript:$callback($data)", null)
        }
    }

    fun clearCallbackListeners() {
        presenter.setInitListener(null)
    }

    // Platform lifecycle methods

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        webView.onResume()
        webView.resumeTimers()
    }

    override fun onPause(owner: LifecycleOwner) {
        webView.onPause()
        super.onPause(owner)
    }

    override fun onDetachedFromWindow() {
        Logger.d("### onDetachedFromWindow")
        if (LiveChat.getInstance().liveChatViewLifecycleScope ==
            LiveChatViewLifecycleScope.WHEN_SHOWN
        ) {
            webView.destroy()
        }
        super.onDetachedFromWindow()
    }

    companion object {
        private const val KEY_WEBVIEW_STATE = "webViewState"
        private const val KEY_SUPER_STATE = "superState"
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return Bundle().apply {
            putParcelable(KEY_SUPER_STATE, superState)
            putBundle(KEY_WEBVIEW_STATE, Bundle().also { webView.saveState(it) })
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            state.getBundle(KEY_WEBVIEW_STATE)?.let { webView.restoreState(it) }
            super.onRestoreInstanceState(state.getParcelable(KEY_SUPER_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }
}
