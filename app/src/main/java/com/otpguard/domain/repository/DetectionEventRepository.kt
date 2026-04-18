package com.otpguard.domain.repository

import com.otpguard.data.local.entity.DetectionEventEntity
import com.otpguard.data.local.entity.DetectionEventWithDetails
import kotlinx.coroutines.flow.Flow

interface DetectionEventRepository {
    fun getAllEvents(): Flow<List<DetectionEventWithDetails>>
    fun getRecentEvents(limit: Int): Flow<List<DetectionEventWithDetails>>
    suspend fun insert(event: DetectionEventEntity): Long
    fun getTotalCount(): Flow<Int>
    fun getCountSince(since: Long): Flow<Int>
    suspend fun getRecentNotificationKeys(since: Long): List<String?>
    suspend fun deleteOlderThan(before: Long)
    suspend fun getAllEventsForHashing(): List<DetectionEventEntity>
    suspend fun updateRowHash(id: Int, hash: String)
    suspend fun getPreviousRowHash(beforeId: Int): String?
}
