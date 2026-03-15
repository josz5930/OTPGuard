package com.otpguard.ui.screen.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otpguard.data.local.entity.RegexRuleEntity
import com.otpguard.domain.repository.RegexRuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegexRulesUiState(
    val rules: List<RegexRuleEntity> = emptyList(),
    val showAddDialog: Boolean = false,
    val testResult: String? = null
)

@HiltViewModel
class RegexRulesViewModel @Inject constructor(
    private val repository: RegexRuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegexRulesUiState())
    val uiState: StateFlow<RegexRulesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllRules().collect { rules ->
                _uiState.update { it.copy(rules = rules) }
            }
        }
    }

    fun toggleRule(id: Int, enabled: Boolean) {
        viewModelScope.launch { repository.setEnabled(id, enabled) }
    }

    fun showAddDialog() { _uiState.update { it.copy(showAddDialog = true) } }
    fun hideAddDialog() { _uiState.update { it.copy(showAddDialog = false) } }

    fun addRule(name: String, pattern: String, description: String?) {
        viewModelScope.launch {
            repository.insert(
                RegexRuleEntity(name = name, pattern = pattern, description = description)
            )
            hideAddDialog()
        }
    }

    fun deleteRule(rule: RegexRuleEntity) {
        viewModelScope.launch { repository.delete(rule) }
    }

    fun testRule(pattern: String, sampleText: String) {
        try {
            val regex = Regex(pattern)
            val matches = regex.containsMatchIn(sampleText)
            _uiState.update { it.copy(testResult = if (matches) "MATCH found!" else "No match.") }
        } catch (e: Exception) {
            _uiState.update { it.copy(testResult = "Invalid regex: ${e.message}") }
        }
    }

    fun clearTestResult() { _uiState.update { it.copy(testResult = null) } }
}
