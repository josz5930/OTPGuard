package com.otpguard.data.repository

import com.otpguard.data.local.dao.InputValidationRuleDao
import com.otpguard.data.local.entity.InputValidationRuleEntity
import com.otpguard.domain.repository.InputValidationRuleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InputValidationRuleRepositoryImpl @Inject constructor(
    private val dao: InputValidationRuleDao
) : InputValidationRuleRepository {

    override fun getAllEnabled(): Flow<List<InputValidationRuleEntity>> = dao.getAllEnabled()

    override suspend fun getRulesForEntity(entity: String): List<InputValidationRuleEntity> =
        dao.getRulesForEntity(entity)

    override suspend fun count(): Int = dao.count()
}
