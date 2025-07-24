package com.livechatinc.livechatwidgetexample.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.livechatinc.chatsdk.LiveChat
import com.livechatinc.chatsdk.src.core.LiveChatViewLifecycleScope
import com.livechatinc.livechatwidgetexample.BuildConfig
import com.livechatinc.livechatwidgetexample.R
import com.livechatinc.livechatwidgetexample.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val customParamViews = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            binding.customerName.setText(settings?.customerName)
            binding.customerEmail.setText(settings?.customerEmail)
            binding.groupId.setText(settings?.groupId)

            clearCustomParamRows()

            settings?.customParams?.forEach { (key, value) ->
                addCustomParamRow(key, value)
            }
        }

        binding.updateInfoButton.setOnClickListener { button ->
            updateCustomerInfo()
            hideKeyboard(button)
        }

        binding.customerEmail.setOnEditorActionListener { editView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(editView)
                true
            } else {
                false
            }
        }

        binding.addParamButton.setOnClickListener {
            addCustomParamRow()
        }

        binding.clearSessionButton.setOnClickListener { LiveChat.getInstance().signOutCustomer() }

        setupModeToggleSetting()
    }

    private fun setupModeToggleSetting() {
        binding.lifecycleModeSwitch.isChecked = viewModel.keepLiveChatViewInMemory

        binding.lifecycleModeSwitch.setOnCheckedChangeListener { view, isChecked ->
            viewModel.updateLifecycleScopeMode(isChecked)
            val viewLifecycleScope = if (isChecked) {
                LiveChatViewLifecycleScope.APP
            } else {
                LiveChatViewLifecycleScope.ACTIVITY
            }

            LiveChat.initialize(
                BuildConfig.LICENSE, view.context.applicationContext, viewLifecycleScope
            )
        }
    }

    private fun clearCustomParamRows() {
        binding.customParamsContainer.removeAllViews()
        customParamViews.clear()
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateCustomerInfo() {
        viewModel.updateCustomerInfo(
            customerName = binding.customerName.text.toString(),
            customerEmail = binding.customerEmail.text.toString(),
            groupId = binding.groupId.text.toString(),
            customParams = collectCustomParams()
        )
    }

    private fun addCustomParamRow(initialKey: String = "", initialValue: String = "") {
        val paramLayout = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_param_row, binding.customParamsContainer, false)

        val keyEditText = paramLayout.findViewById<TextInputEditText>(R.id.param_key)
        val valueEditText = paramLayout.findViewById<TextInputEditText>(R.id.param_value)
        val removeButton = paramLayout.findViewById<ImageButton>(R.id.remove_param_button)

        if (initialKey.isNotEmpty()) {
            keyEditText.setText(initialKey)
        }

        if (initialValue.isNotEmpty()) {
            valueEditText.setText(initialValue)
        }
        removeButton.setOnClickListener {
            binding.customParamsContainer.removeView(paramLayout)
            customParamViews.remove(paramLayout)
        }

        if (initialKey.isEmpty()) {
            keyEditText.requestFocus()
        }

        binding.customParamsContainer.addView(paramLayout)
        customParamViews.add(paramLayout)
    }

    private fun collectCustomParams(): Map<String, String> {
        val params = mutableMapOf<String, String>()

        for (view in customParamViews) {
            val keyEditText = view.findViewById<TextInputEditText>(R.id.param_key)
            val valueEditText = view.findViewById<TextInputEditText>(R.id.param_value)

            val key = keyEditText.text.toString().trim()
            val value = valueEditText.text.toString().trim()

            if (key.isNotEmpty()) {
                params[key] = value
            }
        }

        return params
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
