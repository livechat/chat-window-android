package com.livechatinc.chatwidget.src

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.livechatinc.chatwidget.LiveChatView

internal class LiveChatViewManager(private val context: Context) {
    private var liveChatView: LiveChatView? = null

    fun getLiveChatView(): LiveChatView {
        return liveChatView ?: inflate(context).also {
            liveChatView = it
        }
    }

    private fun inflate(context: Context): LiveChatView {
        return LiveChatView(context, null).apply {
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    fun cleanup() {
        liveChatView = null
    }
}
