package com.otpguard.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.otpguard.data.local.entity.MonitoredAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitoredAppDao {

    @Query("SELECT * FROM monitored_app ORDER BY is_default DESC, display_name ASC")
    fun getAllApps(): Flow<List<MonitoredAppEntity>>

    @Query("SELECT * FROM monitored_app WHERE is_enabled = 1")
    suspend fun getEnabledApps(): List<MonitoredAppEntity>

    @Query("SELECT * FROM monitored_app WHERE package_name = :packageName LIMIT 1")
    suspend fun getByPackageName(packageName: String): MonitoredAppEntity?

    @Query("SELECT * FROM monitored_app WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): MonitoredAppEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(app: MonitoredAppEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(apps: List<MonitoredAppEntity>)

    @Update
    suspend fun update(app: MonitoredAppEntity)

    @Delete
    suspend fun delete(app: MonitoredAppEntity)

    @Query("UPDATE monitored_app SET is_enabled = :enabled, updated_at = :now WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean, now: Long = System.currentTimeMillis() / 1000)

    @Query("SELECT COUNT(*) FROM monitored_app")
    suspend fun count(): Int
}
