package com.otpguard.domain.repository

import com.otpguard.data.local.entity.MonitoredAppEntity
import kotlinx.coroutines.flow.Flow

interface MonitoredAppRepository {
    fun getAllApps(): Flow<List<MonitoredAppEntity>>
    suspend fun getEnabledApps(): List<MonitoredAppEntity>
    suspend fun getByPackageName(packageName: String): MonitoredAppEntity?
    suspend fun getById(id: Int): MonitoredAppEntity?
    suspend fun insert(app: MonitoredAppEntity): Long
    suspend fun insertAll(apps: List<MonitoredAppEntity>)
    suspend fun update(app: MonitoredAppEntity)
    suspend fun delete(app: MonitoredAppEntity)
    suspend fun setEnabled(id: Int, enabled: Boolean)
    suspend fun count(): Int
}
