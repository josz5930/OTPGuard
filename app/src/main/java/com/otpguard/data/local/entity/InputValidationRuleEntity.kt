package com.otpguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "input_validation_rule",
    indices = [
        Index(value = ["target_entity", "target_field", "validation_type"], unique = true)
    ]
)
data class InputValidationRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "target_entity") val targetEntity: String,
    @ColumnInfo(name = "target_field") val targetField: String,
    @ColumnInfo(name = "validation_type") val validationType: String,
    @ColumnInfo(name = "validation_param") val validationParam: String? = null,
    @ColumnInfo(name = "error_message") val errorMessage: String,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis() / 1000
)
