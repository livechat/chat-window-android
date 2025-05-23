package com.livechatinc.chatwidget.src.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.livechatinc.chatwidget.LiveChatView
import com.livechatinc.chatwidget.R

class LiveChatFragment : Fragment() {
    private lateinit var liveChatView: LiveChatView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        liveChatView = inflater.inflate(R.layout.live_chat_view, container, false) as LiveChatView
        liveChatView.init()

        return liveChatView
    }
}
