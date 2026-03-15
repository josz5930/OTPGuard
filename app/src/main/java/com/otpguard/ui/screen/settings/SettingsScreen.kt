package com.otpguard.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Warning Notification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsToggle("Sound", uiState.soundEnabled) { viewModel.setSoundEnabled(it) }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingsToggle("Vibration", uiState.vibrateEnabled) { viewModel.setVibrateEnabled(it) }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Timing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Auto-dismiss after", style = MaterialTheme.typography.bodyMedium)
                    Text("${uiState.autoDismissSeconds} seconds", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = uiState.autoDismissSeconds.toFloat(),
                        onValueChange = { viewModel.setAutoDismissSeconds(it.toInt()) },
                        valueRange = 10f..120f,
                        steps = 10
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Collapse window", style = MaterialTheme.typography.bodyMedium)
                    Text("${uiState.collapseWindowMs / 1000} seconds", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = (uiState.collapseWindowMs / 1000f),
                        onValueChange = { viewModel.setCollapseWindowMs((it * 1000).toLong()) },
                        valueRange = 1f..15f,
                        steps = 13
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("OTP Guard v1.0", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Protects you from sharing OTP codes by monitoring notifications and alerting you when verification codes are detected.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Privacy: No OTP values stored. No network access. No data export.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
