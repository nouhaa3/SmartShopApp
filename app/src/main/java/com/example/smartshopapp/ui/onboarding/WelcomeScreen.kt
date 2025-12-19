package com.example.smartshopapp.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartshopapp.R
import com.example.smartshopapp.ui.theme.OldRose
import com.example.smartshopapp.ui.theme.SpaceIndigo

@Composable
fun WelcomeScreen(
    onStart: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OldRose),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.smartshop),
                contentDescription = "SmartShop Logo",
                modifier = Modifier
                    .size(180.dp)
                    .clickable { onStart() }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Welcome to\nBella Rose Jewellery",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SpaceIndigo,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}
