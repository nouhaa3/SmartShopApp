package com.example.smartshopapp.ui.onboarding

import androidx.annotation.DrawableRes
import com.example.smartshopapp.R

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val image: Int? = null,
    val isWelcome: Boolean = false
)

val onboardingPages = listOf(

    // PAGE 1
    OnboardingPage(
        title = "Timeless Jewellery",
        description = "Discover carefully curated jewellery designed to elevate your elegance.",
        image = R.drawable.onboarding_1
    ),

    // PAGE 2
    OnboardingPage(
        title = "Crafted with Intention",
        description = "Every piece is designed with refined materials and modern artistry.",
        image = R.drawable.onboarding_2
    ),

    // PAGE 3
    OnboardingPage(
        title = "Your Collection Awaits",
        description = "Manage and grow your jewellery collection effortlessly.",
        image = R.drawable.onboarding_3
    )
)
