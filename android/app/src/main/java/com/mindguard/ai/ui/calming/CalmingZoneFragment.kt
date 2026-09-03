package com.mindguard.ai.ui.calming

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.mindguard.ai.R

class CalmingZoneFragment : Fragment() {

    private val affirmations = listOf(
        "“You don’t have to control your thoughts. You just have to stop letting them control you. Breathe in stillness, let go of what is outside your control.”",
        "“This feeling is temporary. You have moved through difficult moments before, and you have the strength to move through this one too.”",
        "“Peace begins the moment you choose not to allow another person or event to control your emotions.”",
        "“Breathe in calm, breathe out tension. Your mind deserves a moment of quiet rest.”"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calming_zone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val cardBreathing: MaterialCardView = view.findViewById(R.id.cardBreathing)
        val btnStartBreathing: MaterialButton = view.findViewById(R.id.btnStartBreathing)
        val cardGrounding: MaterialCardView = view.findViewById(R.id.cardGrounding)
        val btnStartGrounding: MaterialButton = view.findViewById(R.id.btnStartGrounding)
        val tvAffirmationText: TextView = view.findViewById(R.id.tvAffirmationText)
        val bannerCrisis: LinearLayout = view.findViewById(R.id.bannerCrisis)

        // Randomly display an inspiring affirmation
        tvAffirmationText.text = affirmations.random()

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val navigateToBreathing = {
            findNavController().navigate(R.id.action_calmingZone_to_breathing)
        }
        cardBreathing.setOnClickListener { navigateToBreathing() }
        btnStartBreathing.setOnClickListener { navigateToBreathing() }

        val navigateToGrounding = {
            findNavController().navigate(R.id.action_calmingZone_to_grounding)
        }
        cardGrounding.setOnClickListener { navigateToGrounding() }
        btnStartGrounding.setOnClickListener { navigateToGrounding() }

        bannerCrisis.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:14416"))
            startActivity(intent)
        }
    }
}
