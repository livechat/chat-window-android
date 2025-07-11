package com.livechatinc.livechatwidgetexample.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.livechatinc.livechatwidgetexample.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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
        }

        binding.updateInfoButton.setOnClickListener { button ->
            updateCustomerInfo()
            hideKeyboard(button)
        }

        binding.customerEmail.setOnEditorActionListener { editView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                updateCustomerInfo()
                hideKeyboard(editView)
                true
            } else {
                false
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateCustomerInfo() {
        viewModel.updateSettings(
            customerName = binding.customerName.text.toString(),
            customerEmail = binding.customerEmail.text.toString(),
            groupId = binding.groupId.text.toString(),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
