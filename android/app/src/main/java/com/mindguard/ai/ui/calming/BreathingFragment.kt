package com.mindguard.ai.ui.calming

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.mindguard.ai.R

class BreathingFragment : Fragment() {

    private enum class BreathingPhase {
        INHALE, HOLD_IN, EXHALE, HOLD_OUT
    }

    private var selectedDurationSeconds = 60
    private var isPlaying = false
    private var timeRemainingMillis = 60000L
    private var currentPhase = BreathingPhase.INHALE
    private var phaseSecondsRemaining = 4
    private var currentCycle = 1
    private var totalCycles = 4

    private var sessionTimer: CountDownTimer? = null
    private var scaleAnimator: ValueAnimator? = null

    private lateinit var btnMode60s: TextView
    private lateinit var btnMode120s: TextView
    private lateinit var tvTimeRemaining: TextView
    private lateinit var tvCycleCounter: TextView
    private lateinit var tvPhaseInstruction: TextView
    private lateinit var tvPhaseCountdown: TextView
    private lateinit var tvGuidanceCue: TextView
    private lateinit var btnPlayPause: MaterialButton
    private lateinit var btnReset: ImageButton
    private lateinit var viewBreathingCircle: View
    private lateinit var viewHaloCircle: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_breathing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        btnMode60s = view.findViewById(R.id.btnMode60s)
        btnMode120s = view.findViewById(R.id.btnMode120s)
        tvTimeRemaining = view.findViewById(R.id.tvTimeRemaining)
        tvCycleCounter = view.findViewById(R.id.tvCycleCounter)
        tvPhaseInstruction = view.findViewById(R.id.tvPhaseInstruction)
        tvPhaseCountdown = view.findViewById(R.id.tvPhaseCountdown)
        tvGuidanceCue = view.findViewById(R.id.tvGuidanceCue)
        btnPlayPause = view.findViewById(R.id.btnPlayPause)
        btnReset = view.findViewById(R.id.btnReset)
        viewBreathingCircle = view.findViewById(R.id.viewBreathingCircle)
        viewHaloCircle = view.findViewById(R.id.viewHaloCircle)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        btnMode60s.setOnClickListener {
            if (!isPlaying) selectMode(60)
        }

        btnMode120s.setOnClickListener {
            if (!isPlaying) selectMode(120)
        }

        btnPlayPause.setOnClickListener {
            if (isPlaying) {
                pauseExercise()
            } else {
                startExercise()
            }
        }

        btnReset.setOnClickListener {
            resetExercise()
        }

        selectMode(60)
    }

    private fun selectMode(seconds: Int) {
        selectedDurationSeconds = seconds
        totalCycles = seconds / 16 // 16s per box cycle (4+4+4+4)
        if (totalCycles < 1) totalCycles = 1
        timeRemainingMillis = seconds * 1000L

        if (seconds == 60) {
            btnMode60s.setBackgroundResource(R.drawable.bg_role_selected)
            btnMode60s.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnMode120s.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            btnMode120s.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        } else {
            btnMode120s.setBackgroundResource(R.drawable.bg_role_selected)
            btnMode120s.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            btnMode60s.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            btnMode60s.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        }

        resetExercise()
    }

    private fun startExercise() {
        isPlaying = true
        btnPlayPause.text = "Pause"
        btnPlayPause.setIconResource(R.drawable.ic_pause)
        btnMode60s.isEnabled = false
        btnMode120s.isEnabled = false

        sessionTimer?.cancel()
        sessionTimer = object : CountDownTimer(timeRemainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingMillis = millisUntilFinished
                updateTimerDisplay(millisUntilFinished)
                tickBreathingCycle()
            }

            override fun onFinish() {
                completeExercise()
            }
        }.start()

        runPhaseAnimation(currentPhase)
    }

    private fun pauseExercise() {
        isPlaying = false
        btnPlayPause.text = "Resume"
        btnPlayPause.setIconResource(R.drawable.ic_play)
        sessionTimer?.cancel()
        scaleAnimator?.pause()
    }

    private fun resetExercise() {
        sessionTimer?.cancel()
        scaleAnimator?.cancel()
        isPlaying = false
        timeRemainingMillis = selectedDurationSeconds * 1000L
        currentPhase = BreathingPhase.INHALE
        phaseSecondsRemaining = 4
        currentCycle = 1

        btnPlayPause.text = "Start"
        btnPlayPause.setIconResource(R.drawable.ic_play)
        btnMode60s.isEnabled = true
        btnMode120s.isEnabled = true

        updateTimerDisplay(timeRemainingMillis)
        tvCycleCounter.text = "Cycle 1 of $totalCycles"
        tvPhaseInstruction.text = "Inhale"
        tvPhaseCountdown.text = "4s"
        tvGuidanceCue.text = "Inhale gently through your nose, expanding your belly."

        viewBreathingCircle.scaleX = 1.0f
        viewBreathingCircle.scaleY = 1.0f
        viewHaloCircle.scaleX = 1.0f
        viewHaloCircle.scaleY = 1.0f
    }

    private fun updateTimerDisplay(millis: Long) {
        val seconds = (millis / 1000).toInt()
        val m = seconds / 60
        val s = seconds % 60
        tvTimeRemaining.text = String.format("%02d:%02d", m, s)
    }

    private fun tickBreathingCycle() {
        phaseSecondsRemaining--
        if (phaseSecondsRemaining <= 0) {
            // Transition to next phase
            currentPhase = when (currentPhase) {
                BreathingPhase.INHALE -> BreathingPhase.HOLD_IN
                BreathingPhase.HOLD_IN -> BreathingPhase.EXHALE
                BreathingPhase.EXHALE -> BreathingPhase.HOLD_OUT
                BreathingPhase.HOLD_OUT -> {
                    currentCycle++
                    if (currentCycle > totalCycles) currentCycle = totalCycles
                    BreathingPhase.INHALE
                }
            }
            phaseSecondsRemaining = 4
            tvCycleCounter.text = "Cycle $currentCycle of $totalCycles"
            runPhaseAnimation(currentPhase)
        }

        tvPhaseCountdown.text = "${phaseSecondsRemaining}s"
    }

    private fun runPhaseAnimation(phase: BreathingPhase) {
        scaleAnimator?.cancel()

        when (phase) {
            BreathingPhase.INHALE -> {
                tvPhaseInstruction.text = "Inhale"
                tvGuidanceCue.text = "Inhale slowly and deeply through your nose."
                animateCircle(1.0f, 1.45f, 4000)
            }
            BreathingPhase.HOLD_IN -> {
                tvPhaseInstruction.text = "Hold"
                tvGuidanceCue.text = "Gently pause and hold your breath in stillness."
                animateCircle(1.45f, 1.45f, 4000)
            }
            BreathingPhase.EXHALE -> {
                tvPhaseInstruction.text = "Exhale"
                tvGuidanceCue.text = "Release smoothly and slowly through your mouth."
                animateCircle(1.45f, 1.0f, 4000)
            }
            BreathingPhase.HOLD_OUT -> {
                tvPhaseInstruction.text = "Rest"
                tvGuidanceCue.text = "Pause comfortably before the next breath."
                animateCircle(1.0f, 1.0f, 4000)
            }
        }
    }

    private fun animateCircle(startScale: Float, endScale: Float, durationMs: Long) {
        scaleAnimator = ValueAnimator.ofFloat(startScale, endScale).apply {
            duration = durationMs
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                viewBreathingCircle.scaleX = scale
                viewBreathingCircle.scaleY = scale
                viewHaloCircle.scaleX = scale * 1.08f
                viewHaloCircle.scaleY = scale * 1.08f
            }
            start()
        }
    }

    private fun completeExercise() {
        resetExercise()
        tvPhaseInstruction.text = "Well Done"
        tvPhaseCountdown.text = "✨"
        tvGuidanceCue.text = "You've completed your box breathing session. Enjoy this moment of clarity."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sessionTimer?.cancel()
        scaleAnimator?.cancel()
    }
}
