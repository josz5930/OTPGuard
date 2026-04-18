package com.otpguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.otpguard.data.local.entity.DetectionEventEntity
import com.otpguard.data.local.entity.DetectionEventWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionEventDao {

    @Transaction
    @Query("SELECT * FROM detection_event ORDER BY detected_at DESC")
    fun getAllEvents(): Flow<List<DetectionEventWithDetails>>

    @Transaction
    @Query("SELECT * FROM detection_event ORDER BY detected_at DESC LIMIT :limit")
    fun getRecentEvents(limit: Int): Flow<List<DetectionEventWithDetails>>

    @Insert
    suspend fun insert(event: DetectionEventEntity): Long

    @Query("SELECT COUNT(*) FROM detection_event")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM detection_event WHERE detected_at >= :since")
    fun getCountSince(since: Long): Flow<Int>

    @Query("SELECT notification_key FROM detection_event WHERE detected_at >= :since")
    suspend fun getRecentNotificationKeys(since: Long): List<String?>

    @Query("DELETE FROM detection_event WHERE detected_at < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("SELECT * FROM detection_event ORDER BY id ASC")
    suspend fun getAllEventsForHashing(): List<DetectionEventEntity>

    @Query("UPDATE detection_event SET row_hash = :hash WHERE id = :id")
    suspend fun updateRowHash(id: Int, hash: String)

    @Query("SELECT row_hash FROM detection_event WHERE id < :beforeId AND row_hash IS NOT NULL ORDER BY id DESC LIMIT 1")
    suspend fun getPreviousRowHash(beforeId: Int): String?
}
