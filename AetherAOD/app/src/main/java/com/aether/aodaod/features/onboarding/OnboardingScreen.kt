package com.aether.aodaod.features.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aether.aodaod.ui.theme.AMOLEDBlack
import com.aether.aodaod.ui.theme.AetherPrimary
import com.aether.aodaod.ui.theme.AetherSecondary
import com.aether.aodaod.ui.theme.NeonGreen
import kotlinx.coroutines.launch

/**
 * Onboarding page data
 */
data class OnboardingPage(
    val title: String,
    val description: String,
    val gradientColors: List<Int>
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Beautiful AMOLED Display",
        description = "Stunning always-on display with deep blacks and vibrant colors optimized for AMOLED screens.",
        gradientColors = listOf(AetherPrimary, AetherSecondary)
    ),
    OnboardingPage(
        title = "Custom Clock Styles",
        description = "Choose from multiple premium clock designs inspired by Samsung, Pixel, and Nothing OS.",
        gradientColors = listOf(NeonGreen, AetherPrimary)
    ),
    OnboardingPage(
        title = "Battery Efficient",
        description = "Smart burn-in protection and optimized rendering for minimal battery consumption.",
        gradientColors = listOf(AetherSecondary, NeonGreen)
    )
)

/**
 * Premium onboarding screen with smooth animations
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AMOLEDBlack)
    ) {
        // Background gradient
        AnimatedVisibility(
            visible = true,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = onboardingPages[pagerState.currentPage].gradientColors,
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                        .copy(alpha = 0.15f)
                    )
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            
            // Logo/Icon area
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = onboardingPages[pagerState.currentPage].gradientColors
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(Modifier.height(48.dp))
            
            // Content
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { -it } + fadeOut(),
                label = "page-transition"
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = onboardingPages[pagerState.currentPage].title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text(
                        text = onboardingPages[pagerState.currentPage].description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
                    )
                }
            }
            
            Spacer(Modifier.weight(1f))
            
            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                onboardingPages.forEachIndexed { index, _ ->
                    val isSelected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) AetherPrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
            
            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip button
                if (pagerState.currentPage < onboardingPages.lastIndex) {
                    TextButton(onClick = { 
                        scope.launch {
                            pagerState.animateScrollToPage(onboardingPages.lastIndex)
                        }
                    }) {
                        Text("Skip")
                    }
                } else {
                    Spacer(Modifier.width(48.dp))
                }
                
                // Next/Get Started button
                Button(
                    onClick = {
                        if (pagerState.currentPage < onboardingPages.lastIndex) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < onboardingPages.lastIndex) "Next" else "Get Started",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
