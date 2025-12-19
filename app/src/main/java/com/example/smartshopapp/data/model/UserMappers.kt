package com.example.smartshopapp.data.model

import com.example.smartshopapp.data.local.UserProfileEntity

fun UserProfile.toEntity() = UserProfileEntity(
    uid = uid,
    fullName = fullName,
    email = email,
    phone = phone,
    imagePath = imagePath
)

fun UserProfileEntity.toUserProfile() = UserProfile(
    uid = uid,
    fullName = fullName,
    email = email,
    phone = phone,
    imagePath = imagePath
)
