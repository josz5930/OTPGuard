package com.otpguard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.otpguard.ui.screen.home.HomeScreen
import com.otpguard.ui.screen.log.DetectionLogScreen
import com.otpguard.ui.screen.onboarding.OnboardingScreen
import com.otpguard.ui.screen.settings.SettingsScreen

@Composable
fun OtpGuardNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.DetectionLog.route) {
            DetectionLogScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
