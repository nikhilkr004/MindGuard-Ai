package com.mindguard.ai.ui.wellbeing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R

class DigitalWellbeingFragment : Fragment() {

    private val viewModel: WellbeingViewModel by viewModels {
        val appContainer = (requireActivity().application as MindGuardApp).container
        WellbeingViewModelFactory(appContainer.wellbeingRepository, appContainer.authRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_digital_wellbeing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val tvTotalScreenTime: TextView = view.findViewById(R.id.tvTotalScreenTime)
        val tvLongestSession: TextView = view.findViewById(R.id.tvLongestSession)
        val tvLateNightTime: TextView = view.findViewById(R.id.tvLateNightTime)
        val tvUnlocksCount: TextView = view.findViewById(R.id.tvUnlocksCount)
        val tvGuidanceBody: TextView = view.findViewById(R.id.tvGuidanceBody)
        val btnTakeDetox: MaterialButton = view.findViewById(R.id.btnTakeDetox)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        btnTakeDetox.setOnClickListener {
            findNavController().navigate(R.id.action_digitalWellbeing_to_breathing)
        }

        viewModel.digitalWellbeing.observe(viewLifecycleOwner) { metric ->
            if (metric != null) {
                val hours = metric.totalScreenMinutes / 60
                val mins = metric.totalScreenMinutes % 60
                tvTotalScreenTime.text = "${hours}h ${mins}m"

                val longestH = metric.longestSessionMinutes / 60
                val longestM = metric.longestSessionMinutes % 60
                tvLongestSession.text = if (longestH > 0) "${longestH}h ${longestM}m" else "${longestM}m"

                val lateNightH = metric.lateNightMinutes / 60
                val lateNightM = metric.lateNightMinutes % 60
                tvLateNightTime.text = if (lateNightH > 0) "${lateNightH}h ${lateNightM}m" else "${lateNightM}m"

                tvUnlocksCount.text = "${metric.unlocksCount} times"

                if (metric.longestSessionMinutes >= 120) {
                    tvGuidanceBody.text = "You have had a long continuous screen session (over 2 hours). Consider stepping away for 10 minutes to rest your eyes and practice mindful breathing."
                } else if (metric.lateNightMinutes >= 60) {
                    tvGuidanceBody.text = "Elevated late-night screen activity detected. Blue light exposure before bed can disrupt melatonin production. Consider power-down 30m prior to sleep."
                } else {
                    tvGuidanceBody.text = "Your digital habits today are well balanced. Maintain periodic micro-breaks during active usage."
                }
            }
        }
    }
}
