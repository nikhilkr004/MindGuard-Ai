package com.mindguard.ai.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.mindguard.ai.R

class BookingConfirmationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvApptId: TextView = view.findViewById(R.id.tvConfirmedApptId)
        val tvProfName: TextView = view.findViewById(R.id.tvConfirmedProfName)
        val tvMode: TextView = view.findViewById(R.id.tvConfirmedMode)
        val tvTime: TextView = view.findViewById(R.id.tvConfirmedTime)
        val btnReturnHome: MaterialButton = view.findViewById(R.id.btnReturnHome)

        val apptId = arguments?.getString("appointmentId") ?: "#APT-94812"
        val profName = arguments?.getString("professionalName") ?: "Dr. Ananya Sharma"
        val mode = arguments?.getString("consultationMode") ?: "JioCloud HD Video"
        val scheduledTime = arguments?.getString("scheduledTime") ?: "Tomorrow, 10:00 AM"

        tvApptId.text = apptId
        tvProfName.text = profName
        tvMode.text = mode
        tvTime.text = scheduledTime

        btnReturnHome.setOnClickListener {
            findNavController().navigate(R.id.action_confirmation_to_home)
        }
    }
}
