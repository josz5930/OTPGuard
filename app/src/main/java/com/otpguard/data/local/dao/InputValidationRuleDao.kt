package com.otpguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.otpguard.data.local.entity.InputValidationRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InputValidationRuleDao {

    @Query("SELECT * FROM input_validation_rule WHERE is_enabled = 1 ORDER BY target_entity ASC, target_field ASC, id ASC")
    fun getAllEnabled(): Flow<List<InputValidationRuleEntity>>

    @Query("SELECT * FROM input_validation_rule WHERE target_entity = :entity AND is_enabled = 1 ORDER BY target_field ASC, id ASC")
    suspend fun getRulesForEntity(entity: String): List<InputValidationRuleEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(rule: InputValidationRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rules: List<InputValidationRuleEntity>)

    @Query("SELECT COUNT(*) FROM input_validation_rule")
    suspend fun count(): Int
}
