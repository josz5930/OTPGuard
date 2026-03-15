package com.otpguard.ui.screen.rules

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
fun RegexRulesScreen(
    navController: NavController,
    viewModel: RegexRulesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Regex Rules") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Default.Add, "Add Rule")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.rules) { rule ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rule.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                if (rule.description != null) {
                                    Text(rule.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(checked = rule.isEnabled, onCheckedChange = { viewModel.toggleRule(rule.id, it) })
                        }
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = rule.pattern,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                        Row {
                            if (rule.isDefault) {
                                AssistChip(onClick = {}, label = { Text("Default") }, modifier = Modifier.padding(end = 8.dp))
                            }
                            AssistChip(onClick = {}, label = { Text("Priority: ${rule.priority}") })
                            Spacer(Modifier.weight(1f))
                            if (!rule.isDefault) {
                                IconButton(onClick = { viewModel.deleteRule(rule) }) {
                                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Test Rule section in Snackbar
    uiState.testResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.clearTestResult() },
            title = { Text("Test Result") },
            text = { Text(result) },
            confirmButton = { TextButton(onClick = { viewModel.clearTestResult() }) { Text("OK") } }
        )
    }

    // Add Rule Dialog
    if (uiState.showAddDialog) {
        var name by remember { mutableStateOf("") }
        var pattern by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var testText by remember { mutableStateOf("") }
        var testResult by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            title = { Text("Add Custom Rule") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Rule Name") }, singleLine = true)
                    OutlinedTextField(value = pattern, onValueChange = { pattern = it }, label = { Text("Regex Pattern") }, singleLine = true)
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description (optional)") }, singleLine = true)
                    OutlinedTextField(value = testText, onValueChange = { testText = it }, label = { Text("Test Text (optional)") }, singleLine = true)
                    if (testText.isNotBlank() && pattern.isNotBlank()) {
                        TextButton(onClick = {
                            testResult = try {
                                val regex = Regex(pattern)
                                if (regex.containsMatchIn(testText)) "MATCH!" else "No match"
                            } catch (e: Exception) { "Invalid regex: ${e.message}" }
                        }) { Text("Test Rule") }
                    }
                    testResult?.let {
                        Text(it, color = if (it == "MATCH!") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.addRule(name.trim(), pattern.trim(), description.trim().ifBlank { null }) },
                    enabled = name.isNotBlank() && pattern.isNotBlank()
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { viewModel.hideAddDialog() }) { Text("Cancel") } }
        )
    }
}
