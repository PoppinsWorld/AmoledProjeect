package com.aether.aodaod.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aether.aodaod.features.onboarding.OnboardingScreen
import com.aether.aodaod.features.onboarding.OnboardingViewModel
import com.aether.aodaod.features.dashboard.DashboardScreen
import com.aether.aodaod.features.clockgallery.ClockGalleryScreen
import com.aether.aodaod.features.livepreview.LivePreviewScreen
import com.aether.aodaod.features.customization.CustomizationScreen
import com.aether.aodaod.features.theme.ThemeScreen
import com.aether.aodaod.features.edgelighting.EdgeLightingScreen
import com.aether.aodaod.features.schedule.ScheduleScreen
import com.aether.aodaod.features.permissions.PermissionsScreen
import com.aether.aodaod.features.battery.BatteryScreen
import com.aether.aodaod.features.about.AboutScreen
import com.aether.aodaod.features.advanced.AdvancedSettingsScreen
import com.aether.aodaod.features.labs.LabsScreen

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object ClockGallery : Screen("clocks")
    object LivePreview : Screen("preview/{clockStyle}") {
        fun createRoute(clockStyle: String) = "preview/$clockStyle"
    }
    object Customization : Screen("customization/{clockStyle}") {
        fun createRoute(clockStyle: String) = "customization/$clockStyle"
    }
    object Theme : Screen("theme")
    object EdgeLighting : Screen("edge_lighting")
    object Schedule : Screen("schedule")
    object Permissions : Screen("permissions")
    object Battery : Screen("battery")
    object About : Screen("about")
    object Advanced : Screen("advanced")
    object Labs : Screen("labs")
}

/**
 * Main navigation host for the app
 */
@Composable
fun AODNavHost(
    navController: NavHostController = rememberNavController()
) {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val isOnboardingCompleted by onboardingViewModel.isOnboardingCompleted.collectAsStateWithLifecycle(initialValue = false)
    
    val startDestination = if (isOnboardingCompleted) Screen.Dashboard.route else Screen.Onboarding.route
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding flow
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    onboardingViewModel.setOnboardingCompleted(true)
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToClocks = { navController.navigate(Screen.ClockGallery.route) },
                onNavigateToPreview = { style -> navController.navigate(Screen.LivePreview.createRoute(style)) },
                onNavigateToTheme = { navController.navigate(Screen.Theme.route) },
                onNavigateToSettings = { navController.navigate(Screen.Advanced.route) },
                onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) }
            )
        }
        
        // Clock gallery
        composable(Screen.ClockGallery.route) {
            ClockGalleryScreen(
                onClockSelected = { style ->
                    navController.navigate(Screen.Customization.createRoute(style.id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Live preview
        composable(
            route = Screen.LivePreview.route,
            arguments = listOf(navArgument("clockStyle") { type = NavType.StringType })
        ) { backStackEntry ->
            val clockStyle = backStackEntry.arguments?.getString("clockStyle") ?: ""
            LivePreviewScreen(
                clockStyleId = clockStyle,
                onNavigateBack = { navController.popBackStack() },
                onCustomize = { navController.navigate(Screen.Customization.createRoute(clockStyle)) }
            )
        }
        
        // Customization screen
        composable(
            route = Screen.Customization.route,
            arguments = listOf(navArgument("clockStyle") { type = NavType.StringType })
        ) { backStackEntry ->
            val clockStyle = backStackEntry.arguments?.getString("clockStyle") ?: ""
            CustomizationScreen(
                clockStyleId = clockStyle,
                onNavigateBack = { navController.popBackStack() },
                onPreview = { navController.navigate(Screen.LivePreview.createRoute(clockStyle)) }
            )
        }
        
        // Theme screen
        composable(Screen.Theme.route) {
            ThemeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Edge lighting screen
        composable(Screen.EdgeLighting.route) {
            EdgeLightingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Schedule screen
        composable(Screen.Schedule.route) {
            ScheduleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Permissions screen
        composable(Screen.Permissions.route) {
            PermissionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Battery screen
        composable(Screen.Battery.route) {
            BatteryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // About screen
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Advanced settings
        composable(Screen.Advanced.route) {
            AdvancedSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLabs = { navController.navigate(Screen.Labs.route) }
            )
        }
        
        // Experimental labs
        composable(Screen.Labs.route) {
            LabsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
