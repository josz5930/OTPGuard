package com.otpguard.ui.screen.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otpguard.data.local.entity.RegexRuleEntity
import com.otpguard.domain.repository.RegexRuleRepository
import com.otpguard.domain.usecase.ValidateInputUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegexRulesUiState(
    val rules: List<RegexRuleEntity> = emptyList(),
    val showAddDialog: Boolean = false,
    val testResult: String? = null,
    val validationErrors: Map<String, String> = emptyMap()
)

@HiltViewModel
class RegexRulesViewModel @Inject constructor(
    private val repository: RegexRuleRepository,
    private val validateInput: ValidateInputUseCase
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

    fun showAddDialog() { _uiState.update { it.copy(showAddDialog = true, validationErrors = emptyMap()) } }
    fun hideAddDialog() { _uiState.update { it.copy(showAddDialog = false, validationErrors = emptyMap()) } }

    fun addRule(name: String, pattern: String, description: String?) {
        viewModelScope.launch {
            val existingNames = _uiState.value.rules.map { it.name }.toSet()
            val errors = validateInput.execute(
                entity = "regex_rule",
                fields = mapOf(
                    "name" to name,
                    "pattern" to pattern,
                    "description" to description,
                    "priority" to "100"
                ),
                existingValues = mapOf("name" to existingNames)
            ).toMutableMap()
            if (!errors.containsKey("pattern") && pattern.isNotBlank()) {
                try { Regex(pattern) } catch (e: Exception) {
                    errors["pattern"] = "Invalid regex: ${e.message}"
                }
            }
            if (errors.isNotEmpty()) {
                _uiState.update { it.copy(validationErrors = errors) }
                return@launch
            }
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
