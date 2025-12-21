package com.example.smartshopapp.data.model

data class Category(
    val id: String = "",
    val name: String = "",
    val order: Int = 0,       // Ordre d'affichage
    val isActive: Boolean = true
)