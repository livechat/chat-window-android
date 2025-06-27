package com.livechatinc.chatwidget.src.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.livechatinc.chatwidget.LiveChat
import com.livechatinc.chatwidget.LiveChatView
import com.livechatinc.chatwidget.LiveChatViewLifecycleScope
import com.livechatinc.chatwidget.R
import com.livechatinc.chatwidget.src.listeners.LiveChatViewInitListener

class LiveChatActivity : AppCompatActivity() {
    private lateinit var container: ViewGroup
    private lateinit var liveChatView: LiveChatView
    private lateinit var errorView: View
    private lateinit var reloadButton: View
    private lateinit var loadingIndicator: View

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

        liveChatView.setActivityContextOnCreate(this)
        liveChatView.init(initCallbackListener)


        setUpInsets()

        errorView = findViewById(R.id.live_chat_error_view)
        reloadButton = findViewById(R.id.live_chat_error_button)
        loadingIndicator = findViewById(R.id.live_chat_loading_indicator)

        setupReloadButton()
    }

    private fun setUpInsets() {
        val container = findViewById<View>(R.id.live_chat_activity_container)

        ViewCompat.setOnApplyWindowInsetsListener(container) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            ViewCompat.setOnApplyWindowInsetsListener(container, null)
            insets
        }

        // Synchronize container paddings with keyboard animation
        ViewCompat.setWindowInsetsAnimationCallback(container,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: List<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
                    val targetBottomPadding =
                        if (ime.bottom > systemBars.bottom) ime.bottom else systemBars.bottom

                    container.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        targetBottomPadding
                    )

                    return insets
                }
            }
        )
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
