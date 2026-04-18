package com.otpguard.domain.usecase

import com.otpguard.domain.repository.InputValidationRuleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateInputUseCase @Inject constructor(
    private val ruleRepository: InputValidationRuleRepository
) {
    /**
     * Validates a set of field values against rules stored in input_validation_rule.
     *
     * @param entity table/entity name (e.g. "monitored_app", "regex_rule", "warning_template")
     * @param fields current field values keyed by column name
     * @param existingValues for "unique" checks — column name → set of values already present
     * @return map of columnName → first error message; empty when valid
     */
    suspend fun execute(
        entity: String,
        fields: Map<String, String?>,
        existingValues: Map<String, Set<String>> = emptyMap()
    ): Map<String, String> {
        val rules = ruleRepository.getRulesForEntity(entity)
        val errors = mutableMapOf<String, String>()
        for (rule in rules) {
            if (errors.containsKey(rule.targetField)) continue
            val value = fields[rule.targetField]
            val error = checkRule(rule.validationType, rule.validationParam, value, rule.errorMessage, existingValues[rule.targetField])
            if (error != null) errors[rule.targetField] = error
        }
        return errors
    }

    private fun checkRule(
        type: String,
        param: String?,
        value: String?,
        errorMessage: String,
        existing: Set<String>?
    ): String? = when (type) {
        "required" -> if (value.isNullOrBlank()) errorMessage else null
        "max_length" -> {
            val max = param?.toIntOrNull() ?: return null
            if ((value?.length ?: 0) > max) errorMessage else null
        }
        "min_length" -> {
            val min = param?.toIntOrNull() ?: return null
            if ((value?.length ?: 0) < min) errorMessage else null
        }
        "regex_format" -> {
            val pattern = param ?: return null
            if (value.isNullOrEmpty()) null
            else try {
                if (Regex(pattern).matches(value)) null else errorMessage
            } catch (e: Exception) {
                null
            }
        }
        "enum" -> {
            val allowed = param?.split(",")?.map { it.trim() }?.toSet() ?: return null
            if (value != null && value !in allowed) errorMessage else null
        }
        "range_min" -> {
            val min = param?.toLongOrNull() ?: return null
            val n = value?.toLongOrNull()
            if (n != null && n < min) errorMessage else null
        }
        "range_max" -> {
            val max = param?.toLongOrNull() ?: return null
            val n = value?.toLongOrNull()
            if (n != null && n > max) errorMessage else null
        }
        "unique" -> {
            if (!value.isNullOrBlank() && existing != null && value in existing) errorMessage else null
        }
        else -> null
    }
}
