package com.livechatinc.chatwidget.src.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.chatwidget.src.core.LiveChatViewLifecycleScope
import com.livechatinc.chatwidget.R
import com.livechatinc.chatwidget.src.core.managers.WindowInsetManager
import com.livechatinc.chatwidget.src.domain.interfaces.LiveChatViewInitListener

class LiveChatActivity : AppCompatActivity() {
    private lateinit var container: ViewGroup
    private lateinit var liveChatView: LiveChatView
    private lateinit var errorView: View
    private lateinit var reloadButton: View
    private lateinit var loadingIndicator: View
    private lateinit var insetManager: WindowInsetManager

    private val initCallbackListener = object : LiveChatViewInitListener {
        override fun onUIReady() {
            updateViewVisibility(
                loading = false,
                chatVisible = true,
                errorVisible = false
            )
        }

        override fun onHide() {
            finish()
        }

        override fun onError(cause: Throwable) {
            updateViewVisibility(
                loading = false,
                chatVisible = false,
                errorVisible = true
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.live_chat_activity)

        container = findViewById(R.id.live_chat_activity_container)

        if (LiveChat.getInstance().liveChatViewLifecycleScope == LiveChatViewLifecycleScope.APP) {
            liveChatView = LiveChat.getInstance().getLiveChatView()
            (liveChatView.parent as? ViewGroup)?.removeView(liveChatView)

            container.addView(liveChatView)
            if (liveChatView.isUIReady) {
                liveChatView.visibility = View.VISIBLE
            }
        } else {
            liveChatView = LiveChatView(this, null).apply {
                visibility = View.GONE
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            container.addView(liveChatView)
        }

        liveChatView.attachTo(this)
        liveChatView.init(initCallbackListener)

        insetManager = WindowInsetManager(container)
        insetManager.setupInsets()

        errorView = findViewById(R.id.live_chat_error_view)
        reloadButton = findViewById(R.id.live_chat_error_button)
        loadingIndicator = findViewById(R.id.live_chat_loading_indicator)

        setupReloadButton()
    }

    private fun setupReloadButton() {
        reloadButton.setOnClickListener {
            updateViewVisibility(
                loading = true,
                chatVisible = false,
                errorVisible = false
            )

            liveChatView.init()
        }
    }

    private fun updateViewVisibility(
        loading: Boolean,
        chatVisible: Boolean,
        errorVisible: Boolean
    ) {
        loadingIndicator.isVisible = loading
        liveChatView.isVisible = chatVisible
        errorView.isVisible = errorVisible
    }

    override fun onDestroy() {
        if (LiveChat.getInstance().liveChatViewLifecycleScope == LiveChatViewLifecycleScope.APP) {
            liveChatView.clearCallbackListeners()
            container.removeView(liveChatView)
        }

        super.onDestroy()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LiveChatActivity::class.java)

            context.startActivity(intent)
        }
    }

    private object Keys {
        const val KEY_LOADING_INDICATOR_VISIBLE = "loadingIndicatorVisible"
        const val KEY_LIVE_CHAT_VIEW_VISIBLE = "liveChatViewVisible"
        const val KEY_ERROR_VIEW_VISIBLE = "errorViewVisible"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(Keys.KEY_LOADING_INDICATOR_VISIBLE, loadingIndicator.isVisible)
        outState.putBoolean(Keys.KEY_LIVE_CHAT_VIEW_VISIBLE, liveChatView.isVisible)
        outState.putBoolean(Keys.KEY_ERROR_VIEW_VISIBLE, errorView.isVisible)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        loadingIndicator.isVisible =
            savedInstanceState.getBoolean(Keys.KEY_LOADING_INDICATOR_VISIBLE)
        liveChatView.isVisible = savedInstanceState.getBoolean(Keys.KEY_LIVE_CHAT_VIEW_VISIBLE)
        errorView.isVisible = savedInstanceState.getBoolean(Keys.KEY_ERROR_VIEW_VISIBLE)
    }
}
