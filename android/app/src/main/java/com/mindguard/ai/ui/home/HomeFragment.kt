package com.mindguard.ai.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.databinding.FragmentHomeBinding
import com.mindguard.ai.ui.auth.AuthViewModel
import com.mindguard.ai.ui.auth.AuthViewModelFactory
import com.mindguard.ai.ui.wellbeing.RecommendationAdapter
import com.mindguard.ai.ui.wellbeing.WellbeingViewModel
import com.mindguard.ai.ui.wellbeing.WellbeingViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((requireActivity().application as MindGuardApp).container.authRepository)
    }

    private val wellbeingViewModel: WellbeingViewModel by viewModels {
        val appContainer = (requireActivity().application as MindGuardApp).container
        WellbeingViewModelFactory(appContainer.wellbeingRepository, appContainer.authRepository)
    }

    private lateinit var recommendationAdapter: RecommendationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecommendations()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecommendations() {
        recommendationAdapter = RecommendationAdapter { recommendation ->
            when (recommendation.actionType) {
                "BREATHING" -> findNavController().navigate(R.id.action_home_to_breathing)
                "GROUNDING" -> findNavController().navigate(R.id.action_home_to_grounding)
                "DIGITAL_BREAK" -> findNavController().navigate(R.id.action_home_to_digitalWellbeing)
                "BOOKING" -> findNavController().navigate(R.id.action_home_to_professionals)
                else -> findNavController().navigate(R.id.action_home_to_calmingZone)
            }
        }

        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recommendationAdapter
        }
    }

    private fun setupListeners() {
        authViewModel.checkCurrentUser { user ->
            if (user != null && user.displayName.isNotBlank()) {
                binding.tvUserName.text = user.displayName
            }
        }

        binding.btnTakeAssessment.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_questionnaire)
        }

        binding.cardStartAssessment.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_questionnaire)
        }

        binding.cardDailyCheckIn.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_dailyCheckIn)
        }

        binding.btnNavCalming.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_calmingZone)
        }

        binding.btnNavDigitalWellbeing.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_digitalWellbeing)
        }

        binding.btnNavProfessionals.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_professionals)
        }
    }

    private fun observeViewModel() {
        wellbeingViewModel.latestCheckIn.observe(viewLifecycleOwner) { checkIn ->
            if (checkIn != null) {
                val moodText = when (checkIn.moodRating) {
                    1 -> "Very Low (1/5)"
                    2 -> "Down (2/5)"
                    3 -> "Neutral (3/5)"
                    4 -> "Good (4/5)"
                    5 -> "Great (5/5)"
                    else -> "Recorded"
                }
                binding.tvCheckInPromptTitle.text = "Today's Mood: $moodText"
                binding.tvCheckInPromptSubtitle.text = "Stress: ${checkIn.stressRating}/5 • Sleep: ${checkIn.sleepRating}/5"
                binding.tvCheckInActionText.text = "Update →"
            } else {
                binding.tvCheckInPromptTitle.text = "Today's Wellbeing Check-In"
                binding.tvCheckInPromptSubtitle.text = "Track your mood, stress & sleep today"
                binding.tvCheckInActionText.text = "Check In →"
            }
        }

        wellbeingViewModel.recommendations.observe(viewLifecycleOwner) { recommendations ->
            recommendationAdapter.submitList(recommendations)
        }
    }

    override fun onResume() {
        super.onResume()
        wellbeingViewModel.loadLatestData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
