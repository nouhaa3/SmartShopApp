package com.example.smartshopapp.ui.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.smartshopapp.ui.onboarding.OrganicBlobShape

@Composable
fun GlowingImageContainer(
    imageRes: Int?,
    backgroundColor: Color
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(280.dp)
    ) {

        // Glow blob (same color, stronger + blur)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(OrganicBlobShape())
                .background(backgroundColor.copy(alpha = 0.9f))
                .blur(60.dp)
        )

        // Image holder
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(OrganicBlobShape())
                .background(backgroundColor)
        ) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

