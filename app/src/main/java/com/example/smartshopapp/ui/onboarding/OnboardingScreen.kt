package com.example.smartshopapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

    // SAME BACKGROUND FOR FULL PAGE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OldRose)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            // ---------- BUTTON ----------
            LuxuryButton(
                text = if (pagerState.currentPage == onboardingPages.lastIndex)
                    "Get Started"
                else
                    "Continue",
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp),
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
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ---------- SKIP ----------
        if (pagerState.currentPage < onboardingPages.lastIndex) {
            TextButton(
                onClick = onSkip,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .alpha(0.4f)
            ) {
                Text("Skip", color = Color.White)
            }
        }
    }
}
