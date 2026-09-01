package com.mindguard.ai.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mindguard.ai.data.model.User
import com.mindguard.ai.data.model.UserRole
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.isUserLoggedIn } returns false
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoginEmptyCredentialsTriggersError() {
        viewModel.login("", "")
        val state = viewModel.authState.value
        assertTrue(state is Resource.Error)
        assertEquals("Please enter both email and password", (state as Resource.Error).message)
    }

    @Test
    fun testRegisterPasswordMismatchTriggersError() {
        viewModel.register("John", "john@test.com", "secret123", "secret456", UserRole.USER)
        val state = viewModel.authState.value
        assertTrue(state is Resource.Error)
        assertEquals("Passwords do not match", (state as Resource.Error).message)
    }

    @Test
    fun testRegisterShortPasswordTriggersError() {
        viewModel.register("John", "john@test.com", "123", "123", UserRole.USER)
        val state = viewModel.authState.value
        assertTrue(state is Resource.Error)
        assertEquals("Password must be at least 6 characters", (state as Resource.Error).message)
    }

    @Test
    fun testRegisterSuccess() {
        val user = User(uid = "u1", email = "john@test.com", displayName = "John", role = UserRole.USER.name)
        coEvery { authRepository.registerWithEmail("john@test.com", "secret123", "John", UserRole.USER) } returns Resource.Success(user)

        viewModel.register("John", "john@test.com", "secret123", "secret123", UserRole.USER)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state is Resource.Success)
        assertEquals(user, (state as Resource.Success).data)
    }

    @Test
    fun testAcceptConsentSuccess() {
        coEvery { authRepository.acceptConsent() } returns Resource.Success(Unit)

        viewModel.acceptConsent()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.consentState.value
        assertTrue(state is Resource.Success)
        coVerify(exactly = 1) { authRepository.acceptConsent() }
    }
}
