package com.livechatinc.chatwidget.src.presentation

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
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.chatwidget.src.core.LiveChatViewLifecycleScope
import com.livechatinc.chatwidget.R
import com.livechatinc.chatwidget.src.utils.webview.LiveChatViewChromeClient
import com.livechatinc.chatwidget.src.utils.webview.LiveChatViewJSBridge
import com.livechatinc.chatwidget.src.domain.presenters.LiveChatViewPresenter
import com.livechatinc.chatwidget.src.domain.interfaces.LiveChatViewInternal
import com.livechatinc.chatwidget.src.utils.webview.LiveChatViewWebViewClient
import com.livechatinc.chatwidget.src.utils.FileSharing
import com.livechatinc.chatwidget.src.domain.interfaces.LiveChatViewInitListener
import com.livechatinc.chatwidget.src.utils.Logger
import com.livechatinc.chatwidget.src.domain.models.FileChooserMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@SuppressLint("SetJavaScriptEnabled")
class LiveChatView(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs), LiveChatViewInternal, DefaultLifecycleObserver {
    private var fileSharing: FileSharing? = null
    private var webView: WebView
    private var presenter: LiveChatViewPresenter
    private var activityContextRef: WeakReference<Context>? = null

    val isUIReady: Boolean
        get() = presenter.uiReady

    init {
        inflate(context, R.layout.live_chat_widget_internal, this)
        webView = findViewById(R.id.live_chat_webview)
        presenter = LiveChatViewPresenter(this, LiveChat.getInstance().networkClient)

        configureWebView()
    }

    private fun configureWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webSettings.domStorageEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        webView.webChromeClient = LiveChatViewChromeClient(presenter)
        webView.webViewClient = LiveChatViewWebViewClient(presenter)

        webView.addJavascriptInterface(
            LiveChatViewJSBridge(presenter),
            LiveChatViewJSBridge.INTERFACE_NAME,
        )
    }

    /**
     * Sets the Activity context during the onCreate phase of the Activity lifecycle.
     * This ensures the context is available for operations requiring an Activity.
     */
    fun setActivityContextOnCreate(activity: AppCompatActivity) {
        activityContextRef = WeakReference(activity)

        activity.lifecycle.addObserver(this)

        supportFileSharing(activity)
    }

    private fun supportFileSharing(activity: AppCompatActivity) {
        fileSharing = FileSharing(
            activity.activityResultRegistry,
            presenter,
        )
        activity.lifecycle.addObserver(fileSharing!!)
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

        activityContextRef?.get()?.startActivity(intent)
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

    override fun onDestroy(owner: LifecycleOwner) {
        activityContextRef?.clear()
        activityContextRef = null

        fileSharing?.let {
            owner.lifecycle.removeObserver(it)
            fileSharing = null
        }
        owner.lifecycle.removeObserver(this)

        super.onStop(owner)
    }

    override fun onDetachedFromWindow() {
        Logger.d("### onDetachedFromWindow")
        if (LiveChat.getInstance().liveChatViewLifecycleScope ==
            LiveChatViewLifecycleScope.ACTIVITY
        ) {
            webView.apply {
                removeJavascriptInterface(LiveChatViewJSBridge.INTERFACE_NAME)
                webChromeClient = null
                destroy()
            }
        }

        super.onDetachedFromWindow()
    }

    companion object {
        private const val KEY_WEBVIEW_STATE = "webViewState"
        private const val KEY_SUPER_STATE = "superState"
    }

    override fun onSaveInstanceState(): Parcelable {
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
