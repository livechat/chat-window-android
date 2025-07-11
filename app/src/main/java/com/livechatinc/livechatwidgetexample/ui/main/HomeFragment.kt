package com.livechatinc.livechatwidgetexample.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.livechatinc.livechatwidgetexample.BuildConfig
import com.livechatinc.livechatwidgetexample.HomeActivity
import com.livechatinc.livechatwidgetexample.R
import com.livechatinc.livechatwidgetexample.databinding.FragmentMainBinding

class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.showChat.setOnClickListener { showChatCallback() }
        binding.showSettings.setOnClickListener { showSettingsCallback() }
        binding.licenseNumber.text = "License: ${BuildConfig.LICENSE}"
        binding.sdkVersion.text =
            "SDK Version: ${com.livechatinc.chatwidget.BuildConfig.VERSION_NAME}"

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            binding.customerInfoSettings.text = settings.toString()
        }
    }

    private val showChatCallback = {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            (requireActivity() as HomeActivity).showChat()
        }
    }

    private val showSettingsCallback = {
        findNavController().navigate(R.id.navigate_to_settings)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
