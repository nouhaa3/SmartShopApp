package com.example.smartshopapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val quantity: Int,
    val price: Double,
    val imageUri: String?,

    // NOUVEAUX CHAMPS
    val isAvailable: Boolean = true,
    val lowStockThreshold: Int = 5
)