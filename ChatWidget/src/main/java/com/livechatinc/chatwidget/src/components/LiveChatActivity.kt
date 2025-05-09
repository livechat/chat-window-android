package com.livechatinc.chatwidget.src.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.livechatinc.chatwidget.LiveChatView
import com.livechatinc.chatwidget.R
import com.livechatinc.chatwidget.src.LiveChatViewCallbackListener
import com.livechatinc.chatwidget.src.models.ChatMessage

class LiveChatActivity : AppCompatActivity() {
    private lateinit var liveChatView: LiveChatView
    private lateinit var errorView: View
    private lateinit var reloadButton: View
    private lateinit var loadingIndicator: View

    private val callbackListener = object : LiveChatViewCallbackListener {
        override fun onLoaded() {
            updateViewVisibility(
                loading = false,
                chatVisible = true,
                errorVisible = false
            )
        }

        override fun onHide() {
            finish()
        }

        override fun onNewMessage(message: ChatMessage?) {
        }

        override fun onError(cause: Throwable) {
            updateViewVisibility(
                loading = false,
                chatVisible = false,
                errorVisible = true
            )
        }

        override fun onFileChooserActivityNotFound() {
            Toast.makeText(
                this@LiveChatActivity,
                R.string.chat_widget_file_chooser_not_found,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_widget)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        liveChatView = findViewById(R.id.chat_widget_view)
        errorView = findViewById(R.id.chat_widget_error_view)
        reloadButton = findViewById(R.id.chat_widget_error_button)
        loadingIndicator = findViewById(R.id.chat_widget_loading_indicator)

        setupReloadButton()

        liveChatView.init(callbackListener = callbackListener)
    }

    private fun setupReloadButton() {
        reloadButton.setOnClickListener {
            updateViewVisibility(
                loading = true,
                chatVisible = false,
                errorVisible = false
            )

            liveChatView.init(callbackListener = callbackListener)
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
