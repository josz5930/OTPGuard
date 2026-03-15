package com.otpguard.domain.repository

import com.otpguard.data.local.entity.InputValidationRuleEntity
import kotlinx.coroutines.flow.Flow

interface InputValidationRuleRepository {
    fun getAllEnabled(): Flow<List<InputValidationRuleEntity>>
    suspend fun getRulesForEntity(entity: String): List<InputValidationRuleEntity>
    suspend fun count(): Int
}
