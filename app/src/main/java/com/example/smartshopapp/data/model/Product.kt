package com.example.smartshopapp.data.model

data class Product(
    var id: String = "",
    val name: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val imagePath: String? = null
)
