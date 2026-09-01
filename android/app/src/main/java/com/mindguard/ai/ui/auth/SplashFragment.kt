package com.mindguard.ai.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((requireActivity().application as MindGuardApp).container.authRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            if (viewModel.isUserLoggedIn) {
                viewModel.checkCurrentUser { user ->
                    if (user != null && user.consentAccepted) {
                        findNavController().navigate(R.id.action_splash_to_home)
                    } else if (user != null && !user.consentAccepted) {
                        findNavController().navigate(R.id.action_splash_to_consent)
                    } else {
                        findNavController().navigate(R.id.action_splash_to_login)
                    }
                }
            } else {
                findNavController().navigate(R.id.action_splash_to_onboarding)
            }
        }, 1500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
