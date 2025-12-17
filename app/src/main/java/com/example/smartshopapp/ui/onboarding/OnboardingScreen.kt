package com.example.smartshopapp.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to SmartShop",
            description = "Manage your products easily and efficiently."
        ),
        OnboardingPage(
            title = "Track Your Stock",
            description = "Monitor stock levels and avoid shortages."
        ),
        OnboardingPage(
            title = "Smart Statistics",
            description = "Analyze your sales and performance in real time."
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomBar(
                currentPage = pagerState.currentPage,
                pageCount = pages.size,
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                onGetStarted = onGetStarted
            )
        }
    ) { padding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) { page ->
            OnboardingPageContent(pages[page])
        }
    }
}
