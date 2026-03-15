package com.otpguard.data.repository

import com.otpguard.data.local.dao.WarningTemplateDao
import com.otpguard.data.local.entity.WarningTemplateEntity
import com.otpguard.domain.repository.WarningTemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WarningTemplateRepositoryImpl @Inject constructor(
    private val dao: WarningTemplateDao
) : WarningTemplateRepository {

    override fun getAll(): Flow<List<WarningTemplateEntity>> = dao.getAll()

    override suspend fun findByScopeAndRef(scope: String, refId: String?): WarningTemplateEntity? =
        dao.findByScopeAndRef(scope, refId)

    override suspend fun getGlobalDefault(): WarningTemplateEntity? = dao.getGlobalDefault()

    override suspend fun getChannelTemplate(channelId: String): WarningTemplateEntity? =
        dao.getChannelTemplate(channelId)

    override suspend fun getAppTemplate(appId: String): WarningTemplateEntity? =
        dao.getAppTemplate(appId)

    override suspend fun insert(template: WarningTemplateEntity): Long = dao.insert(template)

    override suspend fun update(template: WarningTemplateEntity) = dao.update(template)

    override suspend fun delete(template: WarningTemplateEntity) = dao.delete(template)

    override suspend fun deleteByScopeAndRef(scope: String, refId: String?) =
        dao.deleteByScopeAndRef(scope, refId)

    override suspend fun count(): Int = dao.count()
}
