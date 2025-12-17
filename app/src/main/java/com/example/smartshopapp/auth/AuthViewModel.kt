package com.example.smartshopapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

            if (email.isBlank()) {
                _uiState.value = AuthUiState(error = "Email cannot be empty")
                return@launch
            }

            if (password.length < 6) {
                _uiState.value = AuthUiState(error = "Password must be at least 6 characters")
                return@launch
            }

            try {
                _uiState.value = AuthUiState(loading = true)

                // Firebase Auth (COROUTINE)
                val result = auth
                    .createUserWithEmailAndPassword(email, password)
                    .await()

                val uid = result.user?.uid ?: throw Exception("User ID missing")

                // Save profile
                firestore.collection("users")
                    .document(uid)
                    .set(
                        mapOf(
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )
                    )
                    .await()

                _uiState.value = AuthUiState(success = true)

            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    error = mapFirebaseError(e)
                )
            }
        }
    }

    // -------------------------
    // LOGIN
    // -------------------------
    fun login(email: String, password: String) {
        viewModelScope.launch {

            if (email.isBlank()) {
                _uiState.value = AuthUiState(error = "Email cannot be empty")
                return@launch
            }

            if (password.isBlank()) {
                _uiState.value = AuthUiState(error = "Password cannot be empty")
                return@launch
            }

            try {
                _uiState.value = AuthUiState(loading = true)

                // Firebase Auth (COROUTINE)
                auth.signInWithEmailAndPassword(email, password).await()

                _uiState.value = AuthUiState(success = true)

            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    error = mapFirebaseError(e)
                )
            }
        }
    }

    // -------------------------
    // ERROR MAPPING
    // -------------------------
    private fun mapFirebaseError(e: Exception): String {
        return when (e) {
            is FirebaseAuthInvalidUserException ->
                "No account found with this email."

            is FirebaseAuthInvalidCredentialsException ->
                "Incorrect email or password."

            is FirebaseAuthUserCollisionException ->
                "An account already exists with this email."

            is FirebaseAuthWeakPasswordException ->
                "Password must be at least 6 characters."

            else ->
                "Authentication failed. Please try again."
        }
    }

    fun reset() {
        _uiState.value = AuthUiState()
    }

    fun logout() {
        auth.signOut()
    }
}
