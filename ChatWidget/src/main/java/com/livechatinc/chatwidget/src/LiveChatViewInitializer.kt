package com.livechatinc.chatwidget.src

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.livechatinc.chatwidget.LiveChatView

class LiveChatViewInitializer(private val context: Context) {
    private var liveChatView: LiveChatView? = null

    fun preLoadLiveChat(callbackListener: LiveChatViewCallbackListener? = null) {
        liveChatView = LiveChatView(context, null).apply {
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            init(callbackListener)
        }
    }

    fun getLiveChatView(): LiveChatView? = liveChatView

    fun cleanup() {
        liveChatView = null
    }
}
