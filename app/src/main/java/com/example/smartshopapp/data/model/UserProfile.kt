package com.example.smartshopapp.data.model


data class UserProfile(
    val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val imagePath: String?
)