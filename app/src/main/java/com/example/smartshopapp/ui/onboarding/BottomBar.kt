package com.example.smartshopapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar(
    currentPage: Int,
    pageCount: Int,
    onNext: () -> Unit,
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Dots indicator
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            repeat(pageCount) { index ->
                val color =
                    if (index == currentPage) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .background(color, shape = MaterialTheme.shapes.small)
                )
            }
        }

        // Button
        Button(
            onClick = {
                if (currentPage == pageCount - 1) {
                    onGetStarted()
                } else {
                    onNext()
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                if (currentPage == pageCount - 1)
                    "Get Started"
                else
                    "Next"
            )
        }
    }
}
