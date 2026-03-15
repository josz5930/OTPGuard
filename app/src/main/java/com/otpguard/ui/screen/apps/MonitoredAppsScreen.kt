package com.otpguard.ui.screen.apps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun MonitoredAppsScreen(
    navController: NavController,
    viewModel: MonitoredAppsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitored Apps") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Default.Add, "Add App")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.apps) { app ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (app.isDefault) {
                                AssistChip(onClick = {}, label = { Text("Default") })
                            }
                        }
                        Switch(checked = app.isEnabled, onCheckedChange = { viewModel.toggleApp(app.id, it) })
                        if (!app.isDefault) {
                            IconButton(onClick = { viewModel.deleteApp(app) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        var packageName by remember { mutableStateOf("") }
        var displayName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            title = { Text("Add Monitored App") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = displayName, onValueChange = { displayName = it }, label = { Text("App Name") }, singleLine = true)
                    OutlinedTextField(value = packageName, onValueChange = { packageName = it }, label = { Text("Package Name") }, singleLine = true, placeholder = { Text("com.example.app") })
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.addApp(packageName.trim(), displayName.trim()) },
                    enabled = packageName.isNotBlank() && displayName.isNotBlank()
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideAddDialog() }) { Text("Cancel") }
            }
        )
    }
}
