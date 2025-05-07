package com.livechatinc.chatwidget.src.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.livechatinc.chatwidget.LiveChatView
import com.livechatinc.chatwidget.R
import com.livechatinc.chatwidget.src.LiveChatViewCallbackListener
import com.livechatinc.chatwidget.src.models.ChatMessage

class LiveChatActivity : AppCompatActivity() {
    private lateinit var liveChatView: LiveChatView
    private lateinit var errorView: ViewGroup
    private lateinit var reloadButton: View
    private lateinit var loadingIndicator: View

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

        val callbackListener = object : LiveChatViewCallbackListener {
            override fun onLoaded() {
                loadingIndicator.visibility = View.GONE
                liveChatView.visibility = ViewGroup.VISIBLE
                errorView.visibility = ViewGroup.GONE
            }

            override fun onHide() {
            }

            override fun onNewMessage(message: ChatMessage?) {
            }

            override fun onError(cause: Throwable) {
                loadingIndicator.visibility = View.GONE
                liveChatView.visibility = ViewGroup.GONE
                errorView.visibility = ViewGroup.VISIBLE
            }

            override fun onFileChooserActivityNotFound() {
                TODO("Not yet implemented")
            }
        }
        reloadButton.setOnClickListener {
            loadingIndicator.visibility = View.VISIBLE
            liveChatView.visibility = ViewGroup.GONE
            errorView.visibility = ViewGroup.GONE
            liveChatView.init(callbackListener = callbackListener)
        }

        liveChatView.init(callbackListener = callbackListener)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LiveChatActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        }
    }
}
