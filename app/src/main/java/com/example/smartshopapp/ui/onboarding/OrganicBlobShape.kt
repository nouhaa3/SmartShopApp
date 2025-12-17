package com.example.smartshopapp.ui.onboarding

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class OrganicBlobShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width * 0.1f, size.height * 0.2f)
            cubicTo(
                size.width * 0.9f, 0f,
                size.width, size.height * 0.7f,
                size.width * 0.7f, size.height
            )
            cubicTo(
                size.width * 0.3f, size.height * 1.1f,
                0f, size.height * 0.6f,
                size.width * 0.1f, size.height * 0.2f
            )
            close()
        }
        return Outline.Generic(path)
    }
}
