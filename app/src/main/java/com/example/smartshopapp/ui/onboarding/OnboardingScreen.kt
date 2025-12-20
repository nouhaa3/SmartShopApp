package com.example.smartshopapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartshopapp.ui.onboarding.components.LuxuryButton
import com.example.smartshopapp.ui.theme.OldRose
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onSkip: () -> Unit
) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        OldRose,
                        OldRose.copy(alpha = 0.95f)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            // ---------- PAGE INDICATORS ----------
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                repeat(onboardingPages.size) { index ->
                    Surface(
                        shape = if (pagerState.currentPage == index)
                            RoundedCornerShape(12.dp)
                        else
                            CircleShape,
                        color = if (pagerState.currentPage == index)
                            Color.White
                        else
                            Color.White.copy(alpha = 0.3f),
                        modifier = Modifier
                            .width(if (pagerState.currentPage == index) 32.dp else 8.dp)
                            .height(8.dp)
                    ) {}
                }
            }

            // ---------- PAGER ----------
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->

                val current = onboardingPages[page]

                if (current.isWelcome) {
                    WelcomeScreen(
                        onStart = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                } else {
                    OnboardingItem(page = current)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ---------- ACTION BUTTON ----------
            Surface(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < onboardingPages.lastIndex) {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                        } else {
                            onFinish()
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color.Black.copy(alpha = 0.15f)
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = if (pagerState.currentPage == onboardingPages.lastIndex)
                            "Get Started"
                        else
                            "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OldRose,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

    }
}