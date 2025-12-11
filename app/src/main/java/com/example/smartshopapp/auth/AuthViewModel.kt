package com.example.smartshopapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // ---------------------
    // REGISTER
    // ---------------------
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(loading = true)

            try {
                if (email.isEmpty()) {
                    _uiState.value = AuthUiState(error = "Email cannot be empty")
                    return@launch
                }

                if (password.length < 6) {
                    _uiState.value = AuthUiState(error = "Password must be at least 6 characters")
                    return@launch
                }

                // Simulated success (replace with Firebase)
                _uiState.value = AuthUiState(success = true)

            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Unknown error")
            }
        }
    }

    // ---------------------
    // LOGIN
    // ---------------------
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(loading = true)

            try {
                if (email.isEmpty()) {
                    _uiState.value = AuthUiState(error = "Email cannot be empty")
                    return@launch
                }

                if (password.isEmpty()) {
                    _uiState.value = AuthUiState(error = "Password cannot be empty")
                    return@launch
                }

                // Fake success (replace with Firebase later)
                _uiState.value = AuthUiState(success = true)

            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Unknown error")
            }
        }
    }

    fun reset() {
        _uiState.value = AuthUiState()
    }
}
