package com.livechatinc.chatsdk.src.core.managers

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.livechatinc.chatsdk.src.domain.interfaces.managers.AppScopedLiveChatViewManager
import com.livechatinc.chatsdk.src.presentation.LiveChatView

internal class AppScopedLiveChatViewManagerImpl(private val applicationContext: Context) :
    AppScopedLiveChatViewManager {
    private var liveChatView: LiveChatView? = null

    override fun getLiveChatView(): LiveChatView {
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

    override fun destroyLiveChatView() {
        liveChatView?.let {
            (it.parent as? ViewGroup)?.removeView(it)
            liveChatView = null
        }
    }
}
