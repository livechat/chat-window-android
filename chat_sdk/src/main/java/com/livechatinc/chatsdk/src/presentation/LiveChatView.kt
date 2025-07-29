package com.livechatinc.chatsdk.src.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.livechatinc.chatsdk.LiveChat
import com.livechatinc.chatsdk.src.core.LiveChatViewLifecycleScope
import com.livechatinc.chatsdk.R
import com.livechatinc.chatsdk.src.utils.webview.LiveChatViewChromeClient
import com.livechatinc.chatsdk.src.utils.webview.LiveChatViewJSBridge
import com.livechatinc.chatsdk.src.domain.presenters.LiveChatViewPresenter
import com.livechatinc.chatsdk.src.domain.interfaces.LiveChatViewInternal
import com.livechatinc.chatsdk.src.utils.webview.LiveChatViewWebViewClient
import com.livechatinc.chatsdk.src.utils.FileSharing
import com.livechatinc.chatsdk.src.domain.interfaces.LiveChatViewInitListener
import com.livechatinc.chatsdk.src.utils.Logger
import com.livechatinc.chatsdk.src.domain.models.FilePickerMode
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
    private var currentLifecycleOwner: LifecycleOwner? = null

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
     * This ensures the context is available for operations requiring an Activity:
     * - file sharing
     * - showing external browser on link clicks
     * - setting the WebView background color
     * Must be called in the Activity's `onCreate`
     */
    fun attachTo(activity: ComponentActivity) {
        detachCurrentLifecycleOwner()

        activityContextRef = WeakReference(activity)
        currentLifecycleOwner = activity
        activity.lifecycle.addObserver(this)

        setupFileSharing(activity, activity.lifecycle)
        setWebViewBackgroundColor(activity)
    }

    /**
     * Same as [attachTo] but for Fragments.
     * Must be called in the Fragment's `onCreate`
     * Fragment must be attached to a [ComponentActivity].
     */
    fun attachTo(fragment: Fragment) {
        val activity = fragment.requireActivity() as? ComponentActivity
            ?: throw IllegalArgumentException("Fragment must be attached to a ComponentActivity")

        detachCurrentLifecycleOwner()

        activityContextRef = WeakReference(activity)
        currentLifecycleOwner = fragment
        activity.lifecycle.addObserver(this)

        setupFileSharing(activity, fragment.lifecycle)
        setWebViewBackgroundColor(fragment.requireContext())
    }

    private fun detachCurrentLifecycleOwner() {
        activityContextRef?.clear()
        activityContextRef = null

        currentLifecycleOwner?.let { owner ->
            owner.lifecycle.removeObserver(this)
            fileSharing?.let { fs ->
                owner.lifecycle.removeObserver(fs)
            }
        }
        fileSharing = null
        currentLifecycleOwner = null
    }

    private fun setupFileSharing(activity: ComponentActivity, lifecycle: Lifecycle) {
        fileSharing = FileSharing(
            activity.activityResultRegistry,
            presenter
        )
        lifecycle.addObserver(fileSharing!!)
    }

    private fun setWebViewBackgroundColor(context: Context) {
        val typedArray =
            context.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
        val backgroundColor = typedArray.getColor(0, Color.WHITE)
        typedArray.recycle()

        webView.setBackgroundColor(backgroundColor)
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
        filePickerMode: FilePickerMode,
    ) {
        if (fileSharing == null) {
            Logger.e("File sharing is not set up. Call attachTo() to set it up")

            return
        }

        when (filePickerMode) {
            FilePickerMode.SINGLE -> fileSharing?.selectFile(filePathCallback)
            FilePickerMode.MULTIPLE -> fileSharing?.selectFiles(filePathCallback)
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

    override fun isChatShown(): Boolean {
        return isShown
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
        if (owner == currentLifecycleOwner) {
            detachCurrentLifecycleOwner()
        }

        super.onStop(owner)
    }

    override fun onDetachedFromWindow() {
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
