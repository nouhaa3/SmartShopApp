package com.example.smartshopapp.auth

sealed class AuthResult {
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String?) : AuthResult()
}
