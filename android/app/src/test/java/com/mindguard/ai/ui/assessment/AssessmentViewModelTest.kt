package com.mindguard.ai.ui.assessment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mindguard.ai.data.model.AssessmentResult
import com.mindguard.ai.data.model.CategoryType
import com.mindguard.ai.data.model.Question
import com.mindguard.ai.data.model.QuestionCategory
import com.mindguard.ai.data.model.RiskLevel
import com.mindguard.ai.data.repository.AssessmentRepository
import com.mindguard.ai.data.repository.AuthRepository
import com.mindguard.ai.utils.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val assessmentRepository: AssessmentRepository = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit var viewModel: AssessmentViewModel

    private val mockQuestionsCategory1 = listOf(
        Question(id = "mood_1", category = "mood", text = "Q1", weight = 1.0f),
        Question(id = "mood_2", category = "mood", text = "Q2", weight = 1.0f)
    )

    private val mockCategories = listOf(
        QuestionCategory(
            type = CategoryType.MOOD,
            title = "Mood",
            description = "Mood desc",
            questions = mockQuestionsCategory1
        ),
        QuestionCategory(
            type = CategoryType.ANXIETY,
            title = "Anxiety",
            description = "Anxiety desc",
            questions = listOf(Question(id = "anx_1", category = "anxiety", text = "Q3", weight = 1.0f))
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.currentUserId } returns "user_test_123"
        coEvery { assessmentRepository.getCategories() } returns mockCategories

        viewModel = AssessmentViewModel(assessmentRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadCategoriesInitializesList() {
        assertEquals(2, viewModel.categories.value?.size)
        assertEquals(0, viewModel.currentCategoryIndex.value)
    }

    @Test
    fun testSelectAnswerUpdatesMap() {
        viewModel.selectAnswer("mood_1", 2.0f)
        assertEquals(2.0f, viewModel.answers.value?.get("mood_1"))
    }

    @Test
    fun testNextCategoryWithoutAnsweringFailsValidation() {
        viewModel.selectAnswer("mood_1", 2.0f)

        val advanced = viewModel.nextCategory()
        assertFalse(advanced)
        assertEquals(0, viewModel.currentCategoryIndex.value)
        assertNotNull(viewModel.validationError.value)
    }

    @Test
    fun testNextCategoryWithAllAnsweredAdvancesIndex() {
        viewModel.selectAnswer("mood_1", 2.0f)
        viewModel.selectAnswer("mood_2", 3.0f)

        val advanced = viewModel.nextCategory()
        assertTrue(advanced)
        assertEquals(1, viewModel.currentCategoryIndex.value)
    }

    @Test
    fun testPreviousCategoryDecrementsIndex() {
        viewModel.selectAnswer("mood_1", 2.0f)
        viewModel.selectAnswer("mood_2", 3.0f)
        viewModel.nextCategory()
        assertEquals(1, viewModel.currentCategoryIndex.value)

        val wentBack = viewModel.previousCategory()
        assertTrue(wentBack)
        assertEquals(0, viewModel.currentCategoryIndex.value)
    }

    @Test
    fun testSubmitAssessmentPerformsInference() {
        viewModel.selectAnswer("mood_1", 1.0f)
        viewModel.selectAnswer("mood_2", 2.0f)
        viewModel.selectAnswer("anx_1", 1.0f)

        val mockResult = AssessmentResult(
            assessmentId = "test_assessment_1",
            userId = "user_test_123",
            riskLevel = "LOW",
            overallScore = 0.85f,
            categoryScores = mapOf("MOOD" to 0.15f)
        )
        coEvery { assessmentRepository.performAssessment("user_test_123", any()) } returns Resource.Success(mockResult)

        viewModel.submitAssessment()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.assessmentState.value
        assertTrue(state is Resource.Success)
        assertEquals(mockResult, (state as Resource.Success).data)
        coVerify(exactly = 1) { assessmentRepository.performAssessment("user_test_123", any()) }
    }
}
