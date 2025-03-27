package com.livechatinc.chatwidget.src.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.livechatinc.chatwidget.ChatWidget
import com.livechatinc.chatwidget.R
import com.livechatinc.chatwidget.src.models.ChatWidgetConfig

class ChatWidgetActivity : AppCompatActivity() {
    private lateinit var chatWidget: ChatWidget
    private lateinit var config: ChatWidgetConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_widget)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        chatWidget = findViewById(R.id.chat_widget_view)

        config = readConfig()
        chatWidget.init(config)
    }

    private fun readConfig(): ChatWidgetConfig {
        val configJson = intent.getStringExtra(EXTRA_CONFIG)
            ?: throw IllegalStateException("ChatWidgetConfig is required")

        return Gson().fromJson(configJson, ChatWidgetConfig::class.java)
    }

    companion object {
        private const val EXTRA_CONFIG = "chat_config"

        fun start(context: Context, config: ChatWidgetConfig) {
            val intent = Intent(context, ChatWidgetActivity::class.java).apply {
                putExtra(EXTRA_CONFIG, Gson().toJson(config))
            }
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
