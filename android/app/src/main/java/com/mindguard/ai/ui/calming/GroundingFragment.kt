package com.mindguard.ai.ui.calming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mindguard.ai.R

class GroundingFragment : Fragment() {

    private data class GroundingStep(
        val category: String,
        val instruction: String,
        val examples: String,
        val itemsCount: Int
    )

    private val steps = listOf(
        GroundingStep(
            category = "5 THINGS YOU SEE",
            instruction = "Look around your space. Notice 5 distinct things you can see right now.",
            examples = "Examples: A shadow on the wall, a reflection of light, a pen, texture of the wood, a plant.",
            itemsCount = 5
        ),
        GroundingStep(
            category = "4 THINGS YOU CAN TOUCH",
            instruction = "Notice 4 things you can physically feel against your skin or in your hands.",
            examples = "Examples: The fabric of your clothes, the coolness of a desk, your feet on the floor, your hair.",
            itemsCount = 4
        ),
        GroundingStep(
            category = "3 THINGS YOU HEAR",
            instruction = "Listen closely. Identify 3 distinct sounds in your immediate or distant environment.",
            examples = "Examples: The hum of an AC/fan, distant street traffic, bird calls, the sound of your own breath.",
            itemsCount = 3
        ),
        GroundingStep(
            category = "2 THINGS YOU SMELL",
            instruction = "Take a gentle breath through your nose. Notice 2 aromas around you.",
            examples = "Examples: Fresh air, coffee aroma, scent of soap, fabric, paper, or essential oils.",
            itemsCount = 2
        ),
        GroundingStep(
            category = "1 THING YOU TASTE",
            instruction = "Focus your awareness inside your mouth. Notice 1 taste.",
            examples = "Examples: A sip of cool water, lingering mint, or simply the neutral taste of your mouth.",
            itemsCount = 1
        )
    )

    private var currentStepIndex = 0

    private lateinit var progressGrounding: LinearProgressIndicator
    private lateinit var tvStepIndicator: TextView
    private lateinit var tvSenseCategory: TextView
    private lateinit var tvStepInstruction: TextView
    private lateinit var tvStepExamples: TextView
    private lateinit var cardStepContent: MaterialCardView
    private lateinit var cardCompletion: MaterialCardView
    private lateinit var llNavButtons: LinearLayout
    private lateinit var btnPrevStep: MaterialButton
    private lateinit var btnNextStep: MaterialButton
    private lateinit var btnFinishGrounding: MaterialButton
    private lateinit var checkBoxes: List<CheckBox>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_grounding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        progressGrounding = view.findViewById(R.id.progressGrounding)
        tvStepIndicator = view.findViewById(R.id.tvStepIndicator)
        tvSenseCategory = view.findViewById(R.id.tvSenseCategory)
        tvStepInstruction = view.findViewById(R.id.tvStepInstruction)
        tvStepExamples = view.findViewById(R.id.tvStepExamples)
        cardStepContent = view.findViewById(R.id.cardStepContent)
        cardCompletion = view.findViewById(R.id.cardCompletion)
        llNavButtons = view.findViewById(R.id.llNavButtons)
        btnPrevStep = view.findViewById(R.id.btnPrevStep)
        btnNextStep = view.findViewById(R.id.btnNextStep)
        btnFinishGrounding = view.findViewById(R.id.btnFinishGrounding)

        checkBoxes = listOf(
            view.findViewById(R.id.cbItem1),
            view.findViewById(R.id.cbItem2),
            view.findViewById(R.id.cbItem3),
            view.findViewById(R.id.cbItem4),
            view.findViewById(R.id.cbItem5)
        )

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        btnPrevStep.setOnClickListener {
            if (currentStepIndex > 0) {
                currentStepIndex--
                renderStep(currentStepIndex)
            }
        }

        btnNextStep.setOnClickListener {
            if (currentStepIndex < steps.size - 1) {
                currentStepIndex++
                renderStep(currentStepIndex)
            } else {
                showCompletion()
            }
        }

        btnFinishGrounding.setOnClickListener {
            findNavController().navigateUp()
        }

        renderStep(0)
    }

    private fun renderStep(index: Int) {
        val step = steps[index]
        val progressPercent = ((index + 1) * 100) / steps.size
        progressGrounding.progress = progressPercent
        tvStepIndicator.text = "Step ${index + 1} of ${steps.size}"

        tvSenseCategory.text = step.category
        tvStepInstruction.text = step.instruction
        tvStepExamples.text = step.examples

        // Show/hide checkboxes according to item count
        checkBoxes.forEachIndexed { i, checkBox ->
            if (i < step.itemsCount) {
                checkBox.visibility = View.VISIBLE
                checkBox.isChecked = false
                checkBox.text = "Item ${i + 1} acknowledged"
            } else {
                checkBox.visibility = View.GONE
            }
        }

        btnPrevStep.visibility = if (index > 0) View.VISIBLE else View.GONE
        btnNextStep.text = if (index == steps.size - 1) "Finish Grounding ✨" else "Next Sense →"

        cardStepContent.visibility = View.VISIBLE
        llNavButtons.visibility = View.VISIBLE
        cardCompletion.visibility = View.GONE
    }

    private fun showCompletion() {
        progressGrounding.progress = 100
        tvStepIndicator.text = "Complete"
        cardStepContent.visibility = View.GONE
        llNavButtons.visibility = View.GONE
        cardCompletion.visibility = View.VISIBLE
    }
}
