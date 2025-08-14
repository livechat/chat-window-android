package com.livechatinc.livechatwidgetexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.livechatinc.chatsdk.LiveChat
import com.livechatinc.chatsdk.src.domain.interfaces.UrlHandler

class HomeActivity : AppCompatActivity() {

    private var showChatMode: ShowChatMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        restoreShowChatMode(savedInstanceState)

        LiveChat.getInstance().urlHandler =
            UrlHandler { url ->
                if (url.host.equals("app.settings.example.com")) {
                    when (showChatMode) {
                        ShowChatMode.ACTIVITY -> {
                            // Show desired Activity
                            return@UrlHandler true
                        }

                        ShowChatMode.FRAGMENT -> {
                            findNavController(R.id.main_content).navigate(R.id.navigate_from_live_chat_to_settings)
                            return@UrlHandler true
                        }

                        else -> {
                            return@UrlHandler false
                        }
                    }
                }

                return@UrlHandler false
            }
    }

    fun showChat() {
        showChatMode = ShowChatMode.ACTIVITY
        LiveChat.getInstance().show(this)
    }

    fun showChatUsingFragment() {
        showChatMode = ShowChatMode.FRAGMENT
        findNavController(R.id.main_content).navigate(R.id.navigate_from_home_to_live_chat_activity)
    }

    override fun onDestroy() {
        LiveChat.getInstance().urlHandler = null

        super.onDestroy()
    }

    private fun restoreShowChatMode(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            val modeName = it.getString(KEY_SHOW_CHAT_MODE)
            if (modeName != null) {
                showChatMode = ShowChatMode.valueOf(modeName)
            }
        }
    }

    companion object {
        private const val KEY_SHOW_CHAT_MODE = "show_chat_mode"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        showChatMode?.let {
            outState.putString(KEY_SHOW_CHAT_MODE, it.name)
        }
    }
}

enum class ShowChatMode {
    ACTIVITY,
    FRAGMENT
}
