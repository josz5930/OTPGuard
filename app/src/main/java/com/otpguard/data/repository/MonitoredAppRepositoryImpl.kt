package com.otpguard.data.repository

import com.otpguard.data.local.dao.MonitoredAppDao
import com.otpguard.data.local.entity.MonitoredAppEntity
import com.otpguard.domain.repository.MonitoredAppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonitoredAppRepositoryImpl @Inject constructor(
    private val dao: MonitoredAppDao
) : MonitoredAppRepository {

    override fun getAllApps(): Flow<List<MonitoredAppEntity>> = dao.getAllApps()

    override suspend fun getEnabledApps(): List<MonitoredAppEntity> = dao.getEnabledApps()

    override suspend fun getByPackageName(packageName: String): MonitoredAppEntity? =
        dao.getByPackageName(packageName)

    override suspend fun getById(id: Int): MonitoredAppEntity? = dao.getById(id)

    override suspend fun insert(app: MonitoredAppEntity): Long = dao.insert(app)

    override suspend fun insertAll(apps: List<MonitoredAppEntity>) = dao.insertAll(apps)

    override suspend fun update(app: MonitoredAppEntity) = dao.update(app)

    override suspend fun delete(app: MonitoredAppEntity) = dao.delete(app)

    override suspend fun setEnabled(id: Int, enabled: Boolean) = dao.setEnabled(id, enabled)

    override suspend fun count(): Int = dao.count()
}
