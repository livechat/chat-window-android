package com.livechatinc.chatwidget.src

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.livechatinc.chatwidget.LiveChatView

internal class AppScopedLiveChatViewManager(private val applicationContext: Context) {
    private var liveChatView: LiveChatView? = null

    fun getLiveChatView(): LiveChatView {
        return liveChatView ?: inflate(applicationContext).also {
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

    fun destroyLiveChatView() {
        liveChatView?.let {
            (it.parent as? ViewGroup)?.removeView(it)
            liveChatView = null
        }
    }
}
