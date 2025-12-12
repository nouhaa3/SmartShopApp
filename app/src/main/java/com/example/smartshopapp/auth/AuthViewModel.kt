package com.example.smartshopapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // -------------------------
    // REGISTER
    // -------------------------
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(loading = true)

            if (email.isBlank()) {
                _uiState.value = AuthUiState(error = "Email cannot be empty")
                return@launch
            }

            if (password.length < 6) {
                _uiState.value = AuthUiState(error = "Password must be at least 6 characters")
                return@launch
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                        // Create Firestore profile
                        val userData = mapOf(
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )

                        firestore.collection("users")
                            .document(uid)
                            .set(userData)

                        _uiState.value = AuthUiState(success = true)

                    } else {
                        _uiState.value = AuthUiState(
                            error = task.exception?.message ?: "Registration failed"
                        )
                    }
                }
        }
    }

    // -------------------------
    // LOGIN
    // -------------------------
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(loading = true)

            if (email.isBlank()) {
                _uiState.value = AuthUiState(error = "Email cannot be empty")
                return@launch
            }

            if (password.isBlank()) {
                _uiState.value = AuthUiState(error = "Password cannot be empty")
                return@launch
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState(success = true)
                    } else {
                        _uiState.value = AuthUiState(
                            error = task.exception?.message ?: "Login failed"
                        )
                    }
                }
        }
    }

    // Reset UI state
    fun reset() {
        _uiState.value = AuthUiState()
    }

    // Logout
    fun logout() {
        auth.signOut()
    }
}
