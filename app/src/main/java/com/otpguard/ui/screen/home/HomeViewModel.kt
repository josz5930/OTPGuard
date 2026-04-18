package com.otpguard.ui.screen.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.otpguard.domain.repository.AppConfigRepository
import com.otpguard.domain.repository.DetectionEventRepository
import com.otpguard.util.ConfigKeys
import com.otpguard.util.NotificationAccessHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isServiceActive: Boolean = false,
    val isNotificationAccessGranted: Boolean = false,
    val detectionsToday: Int = 0,
    val detectionsThisWeek: Int = 0,
    val detectionsAllTime: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val appConfigRepository: AppConfigRepository,
    private val detectionEventRepository: DetectionEventRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadState()
    }

    fun refresh() {
        loadState()
    }

    fun toggleService(enabled: Boolean) {
        viewModelScope.launch {
            appConfigRepository.set(ConfigKeys.SERVICE_ENABLED, enabled.toString())
            val accessGranted = NotificationAccessHelper.isNotificationAccessEnabled(getApplication())
            _uiState.update {
                it.copy(
                    isServiceActive = enabled && accessGranted,
                    isNotificationAccessGranted = accessGranted
                )
            }
        }
    }

    private fun loadState() {
        viewModelScope.launch {
            val isServiceEnabled = appConfigRepository.getBoolean(ConfigKeys.SERVICE_ENABLED, true)
            val isAccessGranted = NotificationAccessHelper.isNotificationAccessEnabled(getApplication())
            _uiState.update {
                it.copy(
                    isServiceActive = isServiceEnabled && isAccessGranted,
                    isNotificationAccessGranted = isAccessGranted
                )
            }
        }

        val now = System.currentTimeMillis() / 1000
        val startOfToday = now - (now % 86400)
        val startOfWeek = now - (now % 604800)

        viewModelScope.launch {
            detectionEventRepository.getCountSince(startOfToday).collect { count ->
                _uiState.update { it.copy(detectionsToday = count) }
            }
        }
        viewModelScope.launch {
            detectionEventRepository.getCountSince(startOfWeek).collect { count ->
                _uiState.update { it.copy(detectionsThisWeek = count) }
            }
        }
        viewModelScope.launch {
            detectionEventRepository.getTotalCount().collect { count ->
                _uiState.update { it.copy(detectionsAllTime = count) }
            }
        }
    }
}
