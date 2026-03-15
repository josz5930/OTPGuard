package com.otpguard.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.otpguard.ui.navigation.OtpGuardNavGraph
import com.otpguard.ui.navigation.Screen
import com.otpguard.ui.theme.OtpGuardTheme
import com.otpguard.util.NotificationAccessHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OtpGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination = if (NotificationAccessHelper.isNotificationAccessEnabled(this)) {
                        intent?.getStringExtra("navigate_to")?.let {
                            if (it == "detection_log") Screen.DetectionLog.route else Screen.Home.route
                        } ?: Screen.Home.route
                    } else {
                        Screen.Onboarding.route
                    }

                    OtpGuardNavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
