package com.otpguard.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.otpguard.data.local.entity.RegexRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegexRuleDao {

    @Query("SELECT * FROM regex_rule ORDER BY priority ASC, id ASC")
    fun getAllRules(): Flow<List<RegexRuleEntity>>

    @Query("SELECT * FROM regex_rule WHERE is_enabled = 1 ORDER BY priority ASC")
    suspend fun getEnabledRules(): List<RegexRuleEntity>

    @Query("SELECT * FROM regex_rule WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): RegexRuleEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(rule: RegexRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rules: List<RegexRuleEntity>)

    @Update
    suspend fun update(rule: RegexRuleEntity)

    @Delete
    suspend fun delete(rule: RegexRuleEntity)

    @Query("UPDATE regex_rule SET is_enabled = :enabled, updated_at = :now WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean, now: Long = System.currentTimeMillis() / 1000)

    @Query("SELECT COUNT(*) FROM regex_rule")
    suspend fun count(): Int
}
