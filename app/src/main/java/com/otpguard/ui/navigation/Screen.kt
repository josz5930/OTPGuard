package com.otpguard.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Onboarding : Screen("onboarding")
    data object DetectionLog : Screen("detection_log")
    data object Settings : Screen("settings")
    data object MonitoredApps : Screen("monitored_apps")
    data object RegexRules : Screen("regex_rules")
}
