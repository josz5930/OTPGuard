package com.otpguard.domain.repository

import com.otpguard.data.local.entity.WarningTemplateEntity
import kotlinx.coroutines.flow.Flow

interface WarningTemplateRepository {
    fun getAll(): Flow<List<WarningTemplateEntity>>
    suspend fun findByScopeAndRef(scope: String, refId: String?): WarningTemplateEntity?
    suspend fun getGlobalDefault(): WarningTemplateEntity?
    suspend fun getChannelTemplate(channelId: String): WarningTemplateEntity?
    suspend fun getAppTemplate(appId: String): WarningTemplateEntity?
    suspend fun insert(template: WarningTemplateEntity): Long
    suspend fun update(template: WarningTemplateEntity)
    suspend fun delete(template: WarningTemplateEntity)
    suspend fun deleteByScopeAndRef(scope: String, refId: String?)
    suspend fun count(): Int
}
