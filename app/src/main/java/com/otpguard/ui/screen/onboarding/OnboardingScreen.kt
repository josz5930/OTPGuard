package com.otpguard.ui.screen.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.otpguard.ui.navigation.Screen
import com.otpguard.util.NotificationAccessHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    var isAccessGranted by remember { mutableStateOf(false) }
    var isSensitiveNotifGranted by remember { mutableStateOf(false) }

    val sensitiveNotifLauncher = if (Build.VERSION.SDK_INT >= 35) {
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            isSensitiveNotifGranted = granted
        }
    } else null

    LaunchedEffect(Unit) {
        isAccessGranted = NotificationAccessHelper.isNotificationAccessEnabled(context)
        if (Build.VERSION.SDK_INT >= 35) {
            isSensitiveNotifGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECEIVE_SENSITIVE_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
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
            if (Build.VERSION.SDK_INT >= 35) {
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
                            text = "Android 15 & above: Due to new privacy restrictions, this app may not detect OTPs in some cases. After granting access, go to Settings > Apps > OTP Guard > Notification access and toggle it OFF and back ON if detections are missed.",
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

            // RECEIVE_SENSITIVE_NOTIFICATIONS permission (Android 15+ only)
            if (Build.VERSION.SDK_INT >= 35) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSensitiveNotifGranted)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSensitiveNotifGranted) Icons.Default.CheckCircle else Icons.Default.LockOpen,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (isSensitiveNotifGranted)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Sensitive Notification Access",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Required on Android 15+ so OTP Guard can read the full content of notifications. Without this, OTP codes in messages may be hidden and cannot be detected.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!isSensitiveNotifGranted) {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    sensitiveNotifLauncher?.launch(
                                        Manifest.permission.RECEIVE_SENSITIVE_NOTIFICATIONS
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Grant Permission")
                            }
                        }
                    }
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
