package com.otpguard.data.repository

import com.otpguard.data.local.dao.RegexRuleDao
import com.otpguard.data.local.entity.RegexRuleEntity
import com.otpguard.domain.repository.RegexRuleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegexRuleRepositoryImpl @Inject constructor(
    private val dao: RegexRuleDao
) : RegexRuleRepository {

    override fun getAllRules(): Flow<List<RegexRuleEntity>> = dao.getAllRules()

    override suspend fun getEnabledRules(): List<RegexRuleEntity> = dao.getEnabledRules()

    override suspend fun getById(id: Int): RegexRuleEntity? = dao.getById(id)

    override suspend fun insert(rule: RegexRuleEntity): Long = dao.insert(rule)

    override suspend fun insertAll(rules: List<RegexRuleEntity>) = dao.insertAll(rules)

    override suspend fun update(rule: RegexRuleEntity) = dao.update(rule)

    override suspend fun delete(rule: RegexRuleEntity) = dao.delete(rule)

    override suspend fun setEnabled(id: Int, enabled: Boolean) = dao.setEnabled(id, enabled)

    override suspend fun count(): Int = dao.count()
}
