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
import com.mindguard.ai.databinding.FragmentForgotPasswordBinding
import com.mindguard.ai.utils.Resource

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((requireActivity().application as MindGuardApp).container.authRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSendReset.setOnClickListener {
            val email = binding.etForgotEmail.text?.toString().orEmpty()
            viewModel.sendPasswordReset(email)
        }

        viewModel.passwordResetState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.forgotProgress.visibility = View.VISIBLE
                    binding.btnSendReset.isEnabled = false
                }
                is Resource.Success -> {
                    binding.forgotProgress.visibility = View.GONE
                    binding.btnSendReset.isEnabled = true
                    Toast.makeText(requireContext(), "Password reset link sent to your email!", Toast.LENGTH_LONG).show()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.forgotProgress.visibility = View.GONE
                    binding.btnSendReset.isEnabled = true
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
