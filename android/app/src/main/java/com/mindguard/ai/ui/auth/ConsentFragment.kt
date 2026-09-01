package com.mindguard.ai.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.databinding.FragmentConsentBinding
import com.mindguard.ai.utils.Resource

class ConsentFragment : Fragment() {

    private var _binding: FragmentConsentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((requireActivity().application as MindGuardApp).container.authRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConsentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkBoxesListener = {
            val bothChecked = binding.cbDisclaimer.isChecked && binding.cbPrivacy.isChecked
            binding.btnAcceptConsent.isEnabled = bothChecked
        }

        binding.cbDisclaimer.setOnCheckedChangeListener { _, _ -> checkBoxesListener() }
        binding.cbPrivacy.setOnCheckedChangeListener { _, _ -> checkBoxesListener() }

        binding.btnAcceptConsent.setOnClickListener {
            viewModel.acceptConsent()
        }

        viewModel.consentState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.consentProgress.visibility = View.VISIBLE
                    binding.btnAcceptConsent.isEnabled = false
                }
                is Resource.Success -> {
                    binding.consentProgress.visibility = View.GONE
                    Toast.makeText(requireContext(), "Consent recorded", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_consent_to_home)
                }
                is Resource.Error -> {
                    binding.consentProgress.visibility = View.GONE
                    binding.btnAcceptConsent.isEnabled = true
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
