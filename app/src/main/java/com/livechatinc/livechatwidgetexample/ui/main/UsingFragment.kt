package com.livechatinc.livechatwidgetexample.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.livechatinc.chatsdk.LiveChat
import com.livechatinc.chatsdk.R
import com.livechatinc.chatsdk.src.domain.interfaces.LiveChatViewInitListener
import com.livechatinc.chatsdk.src.presentation.LiveChatView

class LiveChatFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var mainContainer: ViewGroup
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
            findNavController().navigateUp()

        }

        override fun onError(cause: Throwable) {
            updateViewVisibility(
                loading = false,
                chatVisible = false,
                errorVisible = true
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.live_chat_activity, container, false)

        //TODO: use example's layout
        mainContainer = view.findViewById(R.id.live_chat_activity_container)
        errorView = view.findViewById(R.id.live_chat_error_view)
        reloadButton = view.findViewById(R.id.live_chat_error_button)
        loadingIndicator = view.findViewById(R.id.live_chat_loading_indicator)

        liveChatView = if (viewModel.keepLiveChatViewInMemory) {
            LiveChat.getInstance().getLiveChatView().also {
                (it.parent as? ViewGroup)?.removeView(it)

                if (it.isUIReady) {
                    it.visibility = View.VISIBLE
                } else {
                    it.visibility = View.GONE
                }
            }
        } else {
            LiveChatView(requireContext(), null).apply {
                visibility = View.GONE
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }

        mainContainer.addView(liveChatView)
        liveChatView.attachTo(this)
        setupReloadButton()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveChatView.init(initCallbackListener)
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

    override fun onDestroyView() {
        liveChatView.clearCallbackListeners()
        mainContainer.removeView(liveChatView)

        super.onDestroyView()
    }
}
