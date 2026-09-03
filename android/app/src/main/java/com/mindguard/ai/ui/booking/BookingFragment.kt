package com.mindguard.ai.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.data.model.ConsultationMode
import com.mindguard.ai.utils.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingFragment : Fragment() {

    private val viewModel: BookingViewModel by viewModels {
        val appContainer = (requireActivity().application as MindGuardApp).container
        BookingViewModelFactory(appContainer.appointmentRepository)
    }

    private var selectedMode = ConsultationMode.VIDEO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val tvProfName: TextView = view.findViewById(R.id.tvBookingProfName)
        val tvProfTitle: TextView = view.findViewById(R.id.tvBookingProfTitle)
        val tvSlotTime: TextView = view.findViewById(R.id.tvBookingSlotTime)
        val rgMode: RadioGroup = view.findViewById(R.id.rgConsultationMode)
        val rbVideo: RadioButton = view.findViewById(R.id.rbModeVideo)
        val rbAudio: RadioButton = view.findViewById(R.id.rbModeAudio)
        val rbChat: RadioButton = view.findViewById(R.id.rbModeChat)
        val cardVideo: MaterialCardView = view.findViewById(R.id.cardModeVideo)
        val cardAudio: MaterialCardView = view.findViewById(R.id.cardModeAudio)
        val cardChat: MaterialCardView = view.findViewById(R.id.cardModeChat)
        val etNotes: TextInputEditText = view.findViewById(R.id.etBookingNotes)
        val btnConfirm: MaterialButton = view.findViewById(R.id.btnConfirmBooking)
        val progressBooking: ProgressBar = view.findViewById(R.id.progressBooking)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val profId = arguments?.getString("professionalId") ?: "prof_01"
        val profName = arguments?.getString("professionalName") ?: "Dr. Ananya Sharma"
        val profTitle = arguments?.getString("professionalTitle") ?: "Clinical Psychologist"
        val slotId = arguments?.getString("slotId") ?: "slot_01"
        val startTime = arguments?.getLong("startTime") ?: System.currentTimeMillis()
        val endTime = arguments?.getLong("endTime") ?: (System.currentTimeMillis() + 45 * 60 * 1000L)

        tvProfName.text = profName
        tvProfTitle.text = profTitle

        val dateFormat = SimpleDateFormat("EEE, MMM d • hh:mm a", Locale.getDefault())
        tvSlotTime.text = dateFormat.format(Date(startTime))

        // Mode Selection highlighting
        fun updateModeUI(mode: ConsultationMode) {
            selectedMode = mode
            cardVideo.strokeColor = ContextCompat.getColor(requireContext(), if (mode == ConsultationMode.VIDEO) R.color.primary else R.color.card_border)
            cardVideo.setCardBackgroundColor(ContextCompat.getColor(requireContext(), if (mode == ConsultationMode.VIDEO) R.color.primary_container else R.color.surface_light))

            cardAudio.strokeColor = ContextCompat.getColor(requireContext(), if (mode == ConsultationMode.AUDIO) R.color.primary else R.color.card_border)
            cardAudio.setCardBackgroundColor(ContextCompat.getColor(requireContext(), if (mode == ConsultationMode.AUDIO) R.color.primary_container else R.color.surface_light))

            cardChat.strokeColor = ContextCompat.getColor(requireContext(), if (mode == ConsultationMode.CHAT) R.color.primary else R.color.card_border)
            cardChat.setCardBackgroundColor(ContextCompat.getColor(requireContext(), if (mode == ConsultationMode.CHAT) R.color.primary_container else R.color.surface_light))
        }

        rbVideo.setOnClickListener { updateModeUI(ConsultationMode.VIDEO) }
        cardVideo.setOnClickListener { rbVideo.isChecked = true; updateModeUI(ConsultationMode.VIDEO) }

        rbAudio.setOnClickListener { updateModeUI(ConsultationMode.AUDIO) }
        cardAudio.setOnClickListener { rbAudio.isChecked = true; updateModeUI(ConsultationMode.AUDIO) }

        rbChat.setOnClickListener { updateModeUI(ConsultationMode.CHAT) }
        cardChat.setOnClickListener { rbChat.isChecked = true; updateModeUI(ConsultationMode.CHAT) }

        btnConfirm.setOnClickListener {
            val notes = etNotes.text?.toString()?.trim()
            viewModel.bookAppointment(profId, slotId, selectedMode, notes)
        }

        viewModel.bookingStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    btnConfirm.isEnabled = false
                    btnConfirm.text = ""
                    progressBooking.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    btnConfirm.isEnabled = true
                    btnConfirm.text = "Confirm & Book Appointment"
                    progressBooking.visibility = View.GONE

                    val bundle = bundleOf(
                        "appointmentId" to resource.data,
                        "professionalName" to profName,
                        "consultationMode" to when (selectedMode) {
                            ConsultationMode.VIDEO -> "JioCloud HD Video"
                            ConsultationMode.AUDIO -> "JioCloud Voice Call"
                            ConsultationMode.CHAT -> "Realtime 1-on-1 Chat"
                        },
                        "scheduledTime" to dateFormat.format(Date(startTime))
                    )
                    findNavController().navigate(R.id.action_booking_to_confirmation, bundle)
                }
                is Resource.Error -> {
                    btnConfirm.isEnabled = true
                    btnConfirm.text = "Confirm & Book Appointment"
                    progressBooking.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message ?: "Booking failed. Please try another slot.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
