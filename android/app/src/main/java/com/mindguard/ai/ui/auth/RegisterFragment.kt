package com.mindguard.ai.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.data.model.UserRole
import com.mindguard.ai.databinding.FragmentRegisterBinding
import com.mindguard.ai.utils.Resource

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var selectedRole: UserRole = UserRole.USER

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((requireActivity().application as MindGuardApp).container.authRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.resetAuthState()

        setupRoleSelectors()

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text?.toString().orEmpty().trim()
            val email = binding.etRegisterEmail.text?.toString().orEmpty().trim()
            val pass = binding.etRegisterPassword.text?.toString().orEmpty()
            viewModel.register(name, email, pass, pass, selectedRole)
        }

        binding.btnGoToLogin.setOnClickListener {
            viewModel.resetAuthState()
            findNavController().navigateUp()
        }

        viewModel.authState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.registerProgress.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is Resource.Success -> {
                    binding.registerProgress.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show()
                    viewModel.resetAuthState()
                    findNavController().navigate(R.id.action_register_to_consent)
                }
                is Resource.Error -> {
                    binding.registerProgress.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
                null -> {
                    binding.registerProgress.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }

    private fun setupRoleSelectors() {
        binding.cardRoleUser.setOnClickListener {
            selectedRole = UserRole.USER
            updateRoleUI()
        }

        binding.cardRoleProfessional.setOnClickListener {
            selectedRole = UserRole.PROFESSIONAL
            updateRoleUI()
        }
    }

    private fun updateRoleUI() {
        if (selectedRole == UserRole.USER) {
            binding.cardRoleUser.setBackgroundResource(R.drawable.bg_role_selected)
            binding.cardRoleProfessional.setBackgroundResource(R.drawable.bg_role_unselected)
            binding.ivRoleUserIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary))
            binding.tvRoleUserTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            binding.ivRoleProfIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            binding.tvRoleProfTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            binding.llProfessionalFields.visibility = View.GONE
        } else {
            binding.cardRoleUser.setBackgroundResource(R.drawable.bg_role_unselected)
            binding.cardRoleProfessional.setBackgroundResource(R.drawable.bg_role_selected)
            binding.ivRoleUserIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            binding.tvRoleUserTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            binding.ivRoleProfIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary))
            binding.tvRoleProfTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            binding.llProfessionalFields.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
