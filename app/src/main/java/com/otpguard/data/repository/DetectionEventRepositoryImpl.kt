package com.otpguard.data.repository

import com.otpguard.data.local.dao.DetectionEventDao
import com.otpguard.data.local.entity.DetectionEventEntity
import com.otpguard.data.local.entity.DetectionEventWithDetails
import com.otpguard.domain.repository.DetectionEventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetectionEventRepositoryImpl @Inject constructor(
    private val dao: DetectionEventDao
) : DetectionEventRepository {

    override fun getAllEvents(): Flow<List<DetectionEventWithDetails>> = dao.getAllEvents()

    override fun getRecentEvents(limit: Int): Flow<List<DetectionEventWithDetails>> =
        dao.getRecentEvents(limit)

    override suspend fun insert(event: DetectionEventEntity): Long = dao.insert(event)

    override fun getTotalCount(): Flow<Int> = dao.getTotalCount()

    override fun getCountSince(since: Long): Flow<Int> = dao.getCountSince(since)

    override suspend fun getRecentNotificationKeys(since: Long): List<String> =
        dao.getRecentNotificationKeys(since)

    override suspend fun deleteOlderThan(before: Long) = dao.deleteOlderThan(before)

    override suspend fun getAllEventsForHashing(): List<DetectionEventEntity> =
        dao.getAllEventsForHashing()

    override suspend fun updateRowHash(id: Int, hash: String) = dao.updateRowHash(id, hash)

    override suspend fun getPreviousRowHash(beforeId: Int): String? = dao.getPreviousRowHash(beforeId)
}
