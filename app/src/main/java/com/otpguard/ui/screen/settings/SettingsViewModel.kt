package com.otpguard.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otpguard.domain.repository.AppConfigRepository
import com.otpguard.util.ConfigKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val soundEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true,
    val autoDismissSeconds: Int = 60,
    val collapseWindowMs: Long = 5000
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update {
                SettingsUiState(
                    soundEnabled = appConfigRepository.getBoolean(ConfigKeys.WARNING_SOUND_ENABLED, true),
                    vibrateEnabled = appConfigRepository.getBoolean(ConfigKeys.WARNING_VIBRATE_ENABLED, true),
                    autoDismissSeconds = appConfigRepository.getInt(ConfigKeys.WARNING_AUTO_DISMISS_SECONDS, 60),
                    collapseWindowMs = appConfigRepository.getLong(ConfigKeys.COLLAPSE_WINDOW_MS, 5000)
                )
            }
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appConfigRepository.set(ConfigKeys.WARNING_SOUND_ENABLED, enabled.toString())
            _uiState.update { it.copy(soundEnabled = enabled) }
        }
    }

    fun setVibrateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appConfigRepository.set(ConfigKeys.WARNING_VIBRATE_ENABLED, enabled.toString())
            _uiState.update { it.copy(vibrateEnabled = enabled) }
        }
    }

    fun setAutoDismissSeconds(seconds: Int) {
        viewModelScope.launch {
            appConfigRepository.set(ConfigKeys.WARNING_AUTO_DISMISS_SECONDS, seconds.toString())
            _uiState.update { it.copy(autoDismissSeconds = seconds) }
        }
    }

    fun setCollapseWindowMs(ms: Long) {
        viewModelScope.launch {
            appConfigRepository.set(ConfigKeys.COLLAPSE_WINDOW_MS, ms.toString())
            _uiState.update { it.copy(collapseWindowMs = ms) }
        }
    }
}
