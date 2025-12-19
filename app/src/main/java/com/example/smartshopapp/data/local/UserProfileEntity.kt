package com.example.smartshopapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val imagePath: String?
)
