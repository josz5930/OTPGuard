package com.otpguard.ui.screen.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otpguard.data.local.entity.MonitoredAppEntity
import com.otpguard.domain.repository.MonitoredAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MonitoredAppsUiState(
    val apps: List<MonitoredAppEntity> = emptyList(),
    val showAddDialog: Boolean = false
)

@HiltViewModel
class MonitoredAppsViewModel @Inject constructor(
    private val repository: MonitoredAppRepository
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

    fun showAddDialog() { _uiState.update { it.copy(showAddDialog = true) } }
    fun hideAddDialog() { _uiState.update { it.copy(showAddDialog = false) } }

    fun addApp(packageName: String, displayName: String) {
        viewModelScope.launch {
            repository.insert(MonitoredAppEntity(packageName = packageName, displayName = displayName))
            hideAddDialog()
        }
    }

    fun deleteApp(app: MonitoredAppEntity) {
        viewModelScope.launch { repository.delete(app) }
    }
}
