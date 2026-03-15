package com.otpguard.ui.screen.onboarding

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.otpguard.ui.navigation.Screen
import com.otpguard.util.NotificationAccessHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    var isAccessGranted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAccessGranted = NotificationAccessHelper.isNotificationAccessEnabled(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isAccessGranted) Icons.Default.CheckCircle else Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = if (isAccessGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (isAccessGranted) "You're All Set!" else "Enable Notification Access",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = if (isAccessGranted)
                    "OTP Guard is now monitoring your notifications for verification codes. You'll receive a warning whenever an OTP is detected."
                else
                    "OTP Guard needs permission to read your notifications so it can detect OTP codes from SMS, WhatsApp, Telegram, and other messaging apps.\n\nThis is the only permission required — no SMS access, no internet.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Android 15+ specific warning
            if (Build.VERSION.SDK_INT >= 35 && isAccessGranted) {
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Android 15 Privacy Notice: If OTPs aren't being detected, go to Settings > Apps > OTP Guard > Notification access, then toggle it OFF and back ON to grant full notification content access.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            if (isAccessGranted) {
                Button(
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Go to Dashboard")
                }

                // Add button to re-configure notification access
                if (Build.VERSION.SDK_INT >= 35) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Re-configure Notification Access")
                    }
                }
            } else {
                Button(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Open Notification Settings")
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        isAccessGranted = NotificationAccessHelper.isNotificationAccessEnabled(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I've enabled it — Check again")
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Your notification content is processed in-memory only and immediately discarded. No OTP values are ever stored.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
