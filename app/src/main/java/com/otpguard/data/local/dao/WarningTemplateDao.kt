package com.otpguard.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.otpguard.data.local.entity.WarningTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WarningTemplateDao {

    @Query("SELECT * FROM warning_template ORDER BY scope ASC, id ASC")
    fun getAll(): Flow<List<WarningTemplateEntity>>

    @Query("SELECT * FROM warning_template WHERE scope = :scope AND scope_reference_id = :refId LIMIT 1")
    suspend fun findByScopeAndRef(scope: String, refId: String?): WarningTemplateEntity?

    @Query("SELECT * FROM warning_template WHERE scope = 'global' AND scope_reference_id IS NULL LIMIT 1")
    suspend fun getGlobalDefault(): WarningTemplateEntity?

    @Query("SELECT * FROM warning_template WHERE scope = 'channel' AND scope_reference_id = :channelId LIMIT 1")
    suspend fun getChannelTemplate(channelId: String): WarningTemplateEntity?

    @Query("SELECT * FROM warning_template WHERE scope = 'app' AND scope_reference_id = :appId LIMIT 1")
    suspend fun getAppTemplate(appId: String): WarningTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: WarningTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(templates: List<WarningTemplateEntity>)

    @Update
    suspend fun update(template: WarningTemplateEntity)

    @Delete
    suspend fun delete(template: WarningTemplateEntity)

    @Query("DELETE FROM warning_template WHERE scope = :scope AND scope_reference_id = :refId AND is_default = 0")
    suspend fun deleteByScopeAndRef(scope: String, refId: String?)

    @Query("SELECT COUNT(*) FROM warning_template")
    suspend fun count(): Int
}
