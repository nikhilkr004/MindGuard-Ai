package com.mindguard.ai.ui.professionals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.data.model.Professional
import com.mindguard.ai.data.model.Slot
import com.mindguard.ai.utils.Resource

class ProfessionalDetailFragment : Fragment() {

    private val viewModel: ProfessionalsViewModel by viewModels {
        val appContainer = (requireActivity().application as MindGuardApp).container
        ProfessionalsViewModelFactory(appContainer.professionalRepository)
    }

    private lateinit var slotAdapter: SlotAdapter
    private var currentProfessional: Professional? = null
    private var selectedSlot: Slot? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_professional_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val tvName: TextView = view.findViewById(R.id.tvDetailName)
        val ivVerified: ImageView = view.findViewById(R.id.ivDetailVerified)
        val tvTitle: TextView = view.findViewById(R.id.tvDetailTitle)
        val tvQualifications: TextView = view.findViewById(R.id.tvDetailQualifications)
        val tvExperience: TextView = view.findViewById(R.id.tvDetailExperience)
        val tvRating: TextView = view.findViewById(R.id.tvDetailRating)
        val tvFee: TextView = view.findViewById(R.id.tvDetailFee)
        val tvBio: TextView = view.findViewById(R.id.tvDetailBio)
        val tvLanguages: TextView = view.findViewById(R.id.tvDetailLanguages)
        val rvSlots: RecyclerView = view.findViewById(R.id.rvAvailableSlots)
        val tvNoSlots: TextView = view.findViewById(R.id.tvNoSlots)
        val btnProceed: MaterialButton = view.findViewById(R.id.btnProceedToBook)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        slotAdapter = SlotAdapter { slot ->
            selectedSlot = slot
        }

        rvSlots.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = slotAdapter
        }

        val professionalId = arguments?.getString("professionalId") ?: "prof_01"
        viewModel.loadProfessionalDetail(professionalId)

        viewModel.selectedProfessional.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                val prof = resource.data
                currentProfessional = prof
                tvName.text = prof.name
                ivVerified.visibility = if (prof.isVerified) View.VISIBLE else View.GONE
                tvTitle.text = prof.title
                tvQualifications.text = prof.qualifications
                tvExperience.text = "${prof.experienceYears}+ Years"
                tvRating.text = "★ ${prof.rating} (${prof.reviewCount})"
                tvFee.text = "₹${prof.consultationFee.toInt()}"
                tvBio.text = prof.bio
                tvLanguages.text = "🗣 Languages: ${prof.languages.joinToString(", ")}"
            }
        }

        viewModel.availableSlots.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                val slots = resource.data
                slotAdapter.submitList(slots)
                selectedSlot = slots.firstOrNull()
                tvNoSlots.visibility = if (slots.isEmpty()) View.VISIBLE else View.GONE
                btnProceed.isEnabled = slots.isNotEmpty()
            }
        }

        btnProceed.setOnClickListener {
            val prof = currentProfessional
            val slot = selectedSlot
            if (prof == null || slot == null) {
                Toast.makeText(requireContext(), "Please select an available slot.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bundle = bundleOf(
                "professionalId" to prof.professionalId,
                "professionalName" to prof.name,
                "professionalTitle" to prof.title,
                "consultationFee" to prof.consultationFee,
                "slotId" to slot.slotId,
                "startTime" to slot.startTime,
                "endTime" to slot.endTime
            )
            findNavController().navigate(R.id.action_detail_to_booking, bundle)
        }
    }
}
