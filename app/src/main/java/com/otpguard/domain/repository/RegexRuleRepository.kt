package com.otpguard.domain.repository

import com.otpguard.data.local.entity.RegexRuleEntity
import kotlinx.coroutines.flow.Flow

interface RegexRuleRepository {
    fun getAllRules(): Flow<List<RegexRuleEntity>>
    suspend fun getEnabledRules(): List<RegexRuleEntity>
    suspend fun getById(id: Int): RegexRuleEntity?
    suspend fun insert(rule: RegexRuleEntity): Long
    suspend fun insertAll(rules: List<RegexRuleEntity>)
    suspend fun update(rule: RegexRuleEntity)
    suspend fun delete(rule: RegexRuleEntity)
    suspend fun setEnabled(id: Int, enabled: Boolean)
    suspend fun count(): Int
}
