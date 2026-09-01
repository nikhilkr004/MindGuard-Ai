package com.mindguard.ai.ui.assessment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mindguard.ai.R
import com.mindguard.ai.data.model.RiskLevel
import com.mindguard.ai.databinding.FragmentAssessmentResultBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssessmentResultFragment : Fragment() {

    private var _binding: FragmentAssessmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssessmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindResultData()
        setupActions()
    }

    private fun bindResultData() {
        val riskLevelStr = arguments?.getString("KEY_RISK_LEVEL") ?: RiskLevel.LOW.name
        val riskLevel = try {
            RiskLevel.valueOf(riskLevelStr)
        } catch (e: Exception) {
            RiskLevel.LOW
        }

        val overallScore = arguments?.getFloat("KEY_OVERALL_SCORE") ?: 0.85f
        val moodScore = arguments?.getFloat("KEY_SCORE_MOOD") ?: 0.15f
        val anxietyScore = arguments?.getFloat("KEY_SCORE_ANXIETY") ?: 0.20f
        val stressScore = arguments?.getFloat("KEY_SCORE_STRESS") ?: 0.25f
        val sleepScore = arguments?.getFloat("KEY_SCORE_SLEEP") ?: 0.10f

        val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        binding.tvResultTimestamp.text = "Completed ${dateFormat.format(Date())} • On-Device AI"

        // Configure Risk Badge & Explanation
        when (riskLevel) {
            RiskLevel.LOW -> {
                binding.tvRiskBadge.text = "LOW RISK INDICATOR"
                binding.tvRiskBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.risk_low))
                binding.tvRiskScoreConfidence.text = "Overall Indicator: ${(overallScore * 100).toInt()}% Confidence"
                binding.tvRiskExplanation.text = "Your responses indicate minimal distress levels across emotional, cognitive, and somatic wellness dimensions."
            }
            RiskLevel.MODERATE -> {
                binding.tvRiskBadge.text = "MODERATE RISK INDICATOR"
                binding.tvRiskBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.risk_moderate))
                binding.tvRiskScoreConfidence.text = "Overall Indicator: ${(overallScore * 100).toInt()}% Confidence"
                binding.tvRiskExplanation.text = "Your responses indicate moderate emotional fatigue or anxiety indicators. Proactive wellness exercises or speaking to a counselor is recommended."
            }
            RiskLevel.HIGH -> {
                binding.tvRiskBadge.text = "ELEVATED RISK INDICATOR"
                binding.tvRiskBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.risk_high))
                binding.tvRiskScoreConfidence.text = "Overall Indicator: ${(overallScore * 100).toInt()}% Confidence"
                binding.tvRiskExplanation.text = "Your responses reflect significant distress across multiple categories. We strongly encourage scheduling a session with a licensed mental health professional."
            }
        }

        // Sub-scale progress meters
        val moodPercent = (moodScore * 100).toInt().coerceIn(0, 100)
        val anxietyPercent = (anxietyScore * 100).toInt().coerceIn(0, 100)
        val stressPercent = (stressScore * 100).toInt().coerceIn(0, 100)
        val sleepPercent = (sleepScore * 100).toInt().coerceIn(0, 100)

        binding.tvScoreMood.text = "$moodPercent%"
        binding.pbMood.progress = moodPercent

        binding.tvScoreAnxiety.text = "$anxietyPercent%"
        binding.pbAnxiety.progress = anxietyPercent

        binding.tvScoreStress.text = "$stressPercent%"
        binding.pbStress.progress = stressPercent

        binding.tvScoreSleep.text = "$sleepPercent%"
        binding.pbSleep.progress = sleepPercent
    }

    private fun setupActions() {
        binding.btnBackToHome.setOnClickListener {
            findNavController().navigate(R.id.action_assessmentResult_to_home)
        }

        binding.btnBookProfessional.setOnClickListener {
            // For now, return to home / navigation to professionals in Phase 6
            findNavController().navigate(R.id.action_assessmentResult_to_home)
        }

        binding.btnGoToCalming.setOnClickListener {
            // For now, return to home / navigation to Calming Zone in Phase 5
            findNavController().navigate(R.id.action_assessmentResult_to_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
