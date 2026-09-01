package com.mindguard.ai.ui.assessment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindguard.ai.MindGuardApp
import com.mindguard.ai.R
import com.mindguard.ai.data.model.AssessmentResult
import com.mindguard.ai.databinding.FragmentQuestionnaireBinding
import com.mindguard.ai.utils.Resource

class QuestionnaireFragment : Fragment() {

    private var _binding: FragmentQuestionnaireBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssessmentViewModel by viewModels {
        val container = (requireActivity().application as MindGuardApp).container
        AssessmentViewModelFactory(container.assessmentRepository, container.authRepository)
    }

    private lateinit var adapter: QuestionnaireAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionnaireBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = QuestionnaireAdapter { questionId, score ->
            viewModel.selectAnswer(questionId, score)
        }
        binding.rvQuestions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvQuestions.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPreviousCategory.setOnClickListener {
            viewModel.previousCategory()
        }

        binding.btnNextCategory.setOnClickListener {
            val catList = viewModel.categories.value.orEmpty()
            val currentIndex = viewModel.currentCategoryIndex.value ?: 0

            if (currentIndex == catList.size - 1) {
                // Final submission
                viewModel.submitAssessment()
            } else {
                viewModel.nextCategory()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                binding.progressBarCategory.max = categories.size
                updateCategoryUI(viewModel.currentCategoryIndex.value ?: 0)
            }
        }

        viewModel.currentCategoryIndex.observe(viewLifecycleOwner) { index ->
            updateCategoryUI(index)
        }

        viewModel.validationError.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.assessmentState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.inferenceProgress.visibility = View.VISIBLE
                    binding.btnNextCategory.isEnabled = false
                    binding.btnPreviousCategory.isEnabled = false
                }
                is Resource.Success -> {
                    binding.inferenceProgress.visibility = View.GONE
                    binding.btnNextCategory.isEnabled = true
                    binding.btnPreviousCategory.isEnabled = true
                    val result = resource.data
                    viewModel.resetAssessmentState()
                    navigateToResults(result)
                }
                is Resource.Error -> {
                    binding.inferenceProgress.visibility = View.GONE
                    binding.btnNextCategory.isEnabled = true
                    binding.btnPreviousCategory.isEnabled = true
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
                null -> {
                    binding.inferenceProgress.visibility = View.GONE
                    binding.btnNextCategory.isEnabled = true
                    binding.btnPreviousCategory.isEnabled = true
                }
            }
        }
    }

    private fun updateCategoryUI(index: Int) {
        val categories = viewModel.categories.value.orEmpty()
        if (categories.isEmpty() || index !in categories.indices) return

        val category = categories[index]
        binding.tvStepIndicator.text = "Category ${index + 1} of ${categories.size}"
        binding.progressBarCategory.progress = index + 1
        binding.tvCategoryTitle.text = category.title
        binding.tvCategoryDesc.text = category.description

        val startIndex = (index * 6) + 1
        val answers = viewModel.answers.value.orEmpty()
        adapter.submitQuestions(category.questions, answers, startIndex)

        // Scroll to top
        binding.nestedScrollView.smoothScrollTo(0, 0)

        // Button visibility & text
        binding.btnPreviousCategory.visibility = if (index > 0) View.VISIBLE else View.INVISIBLE
        if (index == categories.size - 1) {
            binding.btnNextCategory.text = "Complete & Calculate Risk"
        } else {
            binding.btnNextCategory.text = "Next Category"
        }
    }

    private fun navigateToResults(result: AssessmentResult) {
        val bundle = Bundle().apply {
            putString("KEY_ASSESSMENT_ID", result.assessmentId)
            putString("KEY_RISK_LEVEL", result.riskLevel.name)
            putFloat("KEY_OVERALL_SCORE", result.overallScore)
            putFloat("KEY_SCORE_MOOD", result.categoryScores["MOOD"] ?: 0f)
            putFloat("KEY_SCORE_ANXIETY", result.categoryScores["ANXIETY"] ?: 0f)
            putFloat("KEY_SCORE_STRESS", result.categoryScores["STRESS"] ?: 0f)
            putFloat("KEY_SCORE_SLEEP", result.categoryScores["SLEEP"] ?: 0f)
            putFloat("KEY_SCORE_COGNITIVE", result.categoryScores["COGNITIVE"] ?: 0f)
            putFloat("KEY_SCORE_SOCIAL", result.categoryScores["SOCIAL"] ?: 0f)
        }
        findNavController().navigate(R.id.action_questionnaire_to_assessmentResult, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
