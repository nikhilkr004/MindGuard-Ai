package com.mindguard.ai.ui.wellbeing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.utils.Resource

class DailyCheckInFragment : Fragment() {

    private val viewModel: WellbeingViewModel by viewModels {
        val appContainer = (requireActivity().application as MindGuardApp).container
        WellbeingViewModelFactory(appContainer.wellbeingRepository, appContainer.authRepository)
    }

    private var moodVal = 3
    private var energyVal = 3
    private var stressVal = 3
    private var sleepVal = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val tvMoodLabel: TextView = view.findViewById(R.id.tvMoodLabel)
        val sliderMood: Slider = view.findViewById(R.id.sliderMood)
        val tvEnergyLabel: TextView = view.findViewById(R.id.tvEnergyLabel)
        val sliderEnergy: Slider = view.findViewById(R.id.sliderEnergy)
        val tvStressLabel: TextView = view.findViewById(R.id.tvStressLabel)
        val sliderStress: Slider = view.findViewById(R.id.sliderStress)
        val tvSleepLabel: TextView = view.findViewById(R.id.tvSleepLabel)
        val sliderSleep: Slider = view.findViewById(R.id.sliderSleep)
        val etNotes: TextInputEditText = view.findViewById(R.id.etNotes)
        val btnSave: MaterialButton = view.findViewById(R.id.btnSaveCheckIn)
        val progressSave: ProgressBar = view.findViewById(R.id.progressSave)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Sliders change listeners
        sliderMood.addOnChangeListener { _, value, _ ->
            moodVal = value.toInt()
            tvMoodLabel.text = when (moodVal) {
                1 -> "Very Low (1/5)"
                2 -> "Down (2/5)"
                3 -> "Neutral (3/5)"
                4 -> "Good (4/5)"
                5 -> "Great (5/5)"
                else -> "Neutral ($moodVal/5)"
            }
        }

        sliderEnergy.addOnChangeListener { _, value, _ ->
            energyVal = value.toInt()
            tvEnergyLabel.text = when (energyVal) {
                1 -> "Exhausted (1/5)"
                2 -> "Low (2/5)"
                3 -> "Moderate (3/5)"
                4 -> "High (4/5)"
                5 -> "Vibrant (5/5)"
                else -> "Moderate ($energyVal/5)"
            }
        }

        sliderStress.addOnChangeListener { _, value, _ ->
            stressVal = value.toInt()
            tvStressLabel.text = when (stressVal) {
                1 -> "Serene (1/5)"
                2 -> "Low (2/5)"
                3 -> "Moderate (3/5)"
                4 -> "High (4/5)"
                5 -> "Overwhelmed (5/5)"
                else -> "Moderate ($stressVal/5)"
            }
        }

        sliderSleep.addOnChangeListener { _, value, _ ->
            sleepVal = value.toInt()
            tvSleepLabel.text = when (sleepVal) {
                1 -> "Poor (1/5)"
                2 -> "Restless (2/5)"
                3 -> "Fair (3/5)"
                4 -> "Restful (4/5)"
                5 -> "Excellent (5/5)"
                else -> "Fair ($sleepVal/5)"
            }
        }

        btnSave.setOnClickListener {
            val notes = etNotes.text?.toString()?.trim()
            viewModel.saveCheckIn(moodVal, energyVal, stressVal, sleepVal, notes)
        }

        viewModel.saveCheckInStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    btnSave.isEnabled = false
                    btnSave.text = ""
                    progressSave.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    btnSave.isEnabled = true
                    btnSave.text = "Save Today's Check-In"
                    progressSave.visibility = View.GONE
                    Toast.makeText(requireContext(), "Check-in saved successfully! ✨", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    btnSave.isEnabled = true
                    btnSave.text = "Save Today's Check-In"
                    progressSave.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message ?: "Failed to save check-in", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
