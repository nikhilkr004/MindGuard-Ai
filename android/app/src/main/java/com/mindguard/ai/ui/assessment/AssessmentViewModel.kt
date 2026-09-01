package com.mindguard.ai.ui.assessment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindguard.ai.data.model.AssessmentResult
import com.mindguard.ai.data.model.QuestionCategory
import com.mindguard.ai.data.repository.AssessmentRepository
import com.mindguard.ai.data.repository.AuthRepository
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.launch

class AssessmentViewModel(
    private val assessmentRepository: AssessmentRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _categories = MutableLiveData<List<QuestionCategory>>(emptyList())
    val categories: LiveData<List<QuestionCategory>> = _categories

    private val _currentCategoryIndex = MutableLiveData<Int>(0)
    val currentCategoryIndex: LiveData<Int> = _currentCategoryIndex

    private val _answers = MutableLiveData<MutableMap<String, Float>>(mutableMapOf())
    val answers: LiveData<MutableMap<String, Float>> = _answers

    private val _assessmentState = MutableLiveData<Resource<AssessmentResult>?>()
    val assessmentState: LiveData<Resource<AssessmentResult>?> = _assessmentState

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> = _validationError

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            val loaded = assessmentRepository.getCategories()
            _categories.value = loaded
        }
    }

    fun selectAnswer(questionId: String, score: Float) {
        val currentAnswers = _answers.value ?: mutableMapOf()
        currentAnswers[questionId] = score
        _answers.value = currentAnswers
        _validationError.value = null
    }

    fun nextCategory(): Boolean {
        val catList = _categories.value ?: return false
        val currentIndex = _currentCategoryIndex.value ?: 0
        if (currentIndex !in catList.indices) return false

        val currentCategory = catList[currentIndex]
        val currentAnswers = _answers.value ?: mutableMapOf()

        // Validate all questions in current category are answered
        val unanswered = currentCategory.questions.filter { !currentAnswers.containsKey(it.id) }
        if (unanswered.isNotEmpty()) {
            _validationError.value = "Please answer all ${currentCategory.questions.size} questions before proceeding"
            return false
        }

        if (currentIndex < catList.size - 1) {
            _currentCategoryIndex.value = currentIndex + 1
            return true
        }
        return false
    }

    fun previousCategory(): Boolean {
        val currentIndex = _currentCategoryIndex.value ?: 0
        if (currentIndex > 0) {
            _currentCategoryIndex.value = currentIndex - 1
            _validationError.value = null
            return true
        }
        return false
    }

    fun submitAssessment() {
        val catList = _categories.value ?: return
        val currentAnswers = _answers.value ?: mutableMapOf()

        // Validate total answered questions
        val totalQuestions = catList.flatMap { it.questions }
        val unanswered = totalQuestions.filter { !currentAnswers.containsKey(it.id) }
        if (unanswered.isNotEmpty()) {
            _validationError.value = "Please complete all ${totalQuestions.size} questions to calculate risk accurately."
            return
        }

        _assessmentState.value = Resource.Loading
        viewModelScope.launch {
            val userId = authRepository.currentUserId.orEmpty()
            val result = assessmentRepository.performAssessment(userId, currentAnswers)
            _assessmentState.value = result
        }
    }

    fun resetAssessmentState() {
        _assessmentState.value = null
        _validationError.value = null
    }
}
