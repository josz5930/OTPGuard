package com.otpguard.ui.screen.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otpguard.data.local.entity.MonitoredAppEntity
import com.otpguard.domain.repository.MonitoredAppRepository
import com.otpguard.domain.usecase.ValidateInputUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MonitoredAppsUiState(
    val apps: List<MonitoredAppEntity> = emptyList(),
    val showAddDialog: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap()
)

@HiltViewModel
class MonitoredAppsViewModel @Inject constructor(
    private val repository: MonitoredAppRepository,
    private val validateInput: ValidateInputUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoredAppsUiState())
    val uiState: StateFlow<MonitoredAppsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllApps().collect { apps ->
                _uiState.update { it.copy(apps = apps) }
            }
        }
    }

    fun toggleApp(id: Int, enabled: Boolean) {
        viewModelScope.launch { repository.setEnabled(id, enabled) }
    }

    fun showAddDialog() { _uiState.update { it.copy(showAddDialog = true, validationErrors = emptyMap()) } }
    fun hideAddDialog() { _uiState.update { it.copy(showAddDialog = false, validationErrors = emptyMap()) } }

    fun addApp(packageName: String, displayName: String) {
        viewModelScope.launch {
            val existing = _uiState.value.apps.map { it.packageName }.toSet()
            val errors = validateInput.execute(
                entity = "monitored_app",
                fields = mapOf(
                    "package_name" to packageName,
                    "display_name" to displayName,
                    "channel" to "other"
                ),
                existingValues = mapOf("package_name" to existing)
            )
            if (errors.isNotEmpty()) {
                _uiState.update { it.copy(validationErrors = errors) }
                return@launch
            }
            repository.insert(MonitoredAppEntity(packageName = packageName, displayName = displayName))
            hideAddDialog()
        }
    }

    fun deleteApp(app: MonitoredAppEntity) {
        viewModelScope.launch { repository.delete(app) }
    }
}
