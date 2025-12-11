package com.example.smartshopapp.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _uiState.value = AuthUiState(loading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _uiState.value = AuthUiState(loading = false)
                onSuccess()
            }
            .addOnFailureListener { e ->
                _uiState.value = AuthUiState(loading = false, error = e.message)
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        _uiState.value = AuthUiState(loading = true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _uiState.value = AuthUiState(loading = false)
                onSuccess()
            }
            .addOnFailureListener { e ->
                _uiState.value = AuthUiState(loading = false, error = e.message)
            }
    }
}
