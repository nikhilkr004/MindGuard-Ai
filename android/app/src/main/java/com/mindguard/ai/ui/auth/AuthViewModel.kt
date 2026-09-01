package com.mindguard.ai.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindguard.ai.data.model.User
import com.mindguard.ai.data.model.UserRole
import com.mindguard.ai.data.repository.AuthRepository
import com.mindguard.ai.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableLiveData<Resource<User>?>()
    val authState: LiveData<Resource<User>?> = _authState

    private val _consentState = MutableLiveData<Resource<Unit>?>()
    val consentState: LiveData<Resource<Unit>?> = _consentState

    private val _passwordResetState = MutableLiveData<Resource<Unit>?>()
    val passwordResetState: LiveData<Resource<Unit>?> = _passwordResetState

    val isUserLoggedIn: Boolean
        get() = authRepository.isUserLoggedIn

    fun resetAuthState() {
        _authState.value = null
    }

    fun resetConsentState() {
        _consentState.value = null
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = Resource.Error("Please enter both email and password")
            return
        }

        _authState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.loginWithEmail(email.trim(), pass)
            _authState.value = result
        }
    }

    fun register(name: String, email: String, pass: String, confirmPass: String, role: UserRole) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _authState.value = Resource.Error("All fields are required")
            return
        }
        if (pass != confirmPass) {
            _authState.value = Resource.Error("Passwords do not match")
            return
        }
        if (pass.length < 6) {
            _authState.value = Resource.Error("Password must be at least 6 characters")
            return
        }

        _authState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(email.trim(), pass, name.trim(), role)
            _authState.value = result
        }
    }

    fun acceptConsent() {
        _consentState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.acceptConsent()
            _consentState.value = result
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _passwordResetState.value = Resource.Error("Please enter your email address")
            return
        }

        _passwordResetState.value = Resource.Loading
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email.trim())
            _passwordResetState.value = result
        }
    }

    fun checkCurrentUser(onResult: (User?) -> Unit) {
        viewModelScope.launch {
            when (val res = authRepository.getCurrentUserProfile()) {
                is Resource.Success -> onResult(res.data)
                else -> onResult(null)
            }
        }
    }
}
