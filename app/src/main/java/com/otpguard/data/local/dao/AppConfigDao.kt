package com.otpguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.otpguard.data.local.entity.AppConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppConfigDao {

    @Query("SELECT * FROM app_config WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): AppConfigEntity?

    @Query("SELECT * FROM app_config WHERE `key` = :key LIMIT 1")
    fun observe(key: String): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_config")
    fun getAll(): Flow<List<AppConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(config: AppConfigEntity)

    @Query("DELETE FROM app_config WHERE `key` = :key")
    suspend fun delete(key: String)
}
