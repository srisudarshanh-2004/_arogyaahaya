package com.arogyasahaya.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arogyasahaya.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Handles all login / register / logout logic.
 * Fragments observe authState to react to login events.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository,
) : ViewModel() {

    val authState = MutableLiveData<AuthState>(AuthState.Idle)

    // ── Register ──────────────────────────────────────────────────────────────

    fun registerWithEmail(email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            authState.value = AuthState.Error("Passwords do not match")
            return
        }
        viewModelScope.launch {
            authState.value = AuthState.Loading
            val result = profileRepository.registerWithEmail(email, password)
            authState.value = when (result) {
                is UserProfileRepository.RegisterResult.Success -> AuthState.Success("Registered successfully!")
                is UserProfileRepository.RegisterResult.Error  -> AuthState.Error(result.message)
            }
        }
    }

    fun registerWithMobile(mobile: String) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            val result = profileRepository.registerWithMobile(mobile)
            authState.value = when (result) {
                is UserProfileRepository.RegisterResult.Success -> AuthState.Success("OTP sent! (Simulated)")
                is UserProfileRepository.RegisterResult.Error  -> AuthState.Error(result.message)
            }
        }
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            val result = profileRepository.loginWithEmail(email, password)
            authState.value = when (result) {
                is UserProfileRepository.LoginResult.Success -> AuthState.Success("Welcome back!")
                is UserProfileRepository.LoginResult.Error  -> AuthState.Error(result.message)
            }
        }
    }

    fun loginWithMobile(mobile: String) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            val result = profileRepository.loginWithMobile(mobile)
            authState.value = when (result) {
                is UserProfileRepository.LoginResult.Success -> AuthState.Success("Welcome back!")
                is UserProfileRepository.LoginResult.Error  -> AuthState.Error(result.message)
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    fun logout() {
        viewModelScope.launch {
            profileRepository.logout()
            authState.value = AuthState.LoggedOut
        }
    }

    // ── Check login status ────────────────────────────────────────────────────

    fun checkLoginStatus(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(profileRepository.isLoggedIn())
        }
    }

    fun resetState() {
        authState.value = AuthState.Idle
    }

    // ── State sealed class ────────────────────────────────────────────────────

    sealed class AuthState {
        object Idle      : AuthState()
        object Loading   : AuthState()
        object LoggedOut : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val message: String)   : AuthState()
    }
}
