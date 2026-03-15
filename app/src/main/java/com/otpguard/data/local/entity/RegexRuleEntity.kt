package com.otpguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "regex_rule",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["priority", "id"])
    ]
)
data class RegexRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "pattern") val pattern: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = true,
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
    @ColumnInfo(name = "priority") val priority: Int = 100,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis() / 1000,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis() / 1000
)
