package com.example.smartshopapp.auth

data class AuthUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
