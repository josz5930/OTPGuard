package com.otpguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "warning_template",
    indices = [
        Index(value = ["scope", "scope_reference_id"], unique = true)
    ]
)
data class WarningTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "scope") val scope: String,
    @ColumnInfo(name = "scope_reference_id") val scopeReferenceId: String? = null,
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis() / 1000,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis() / 1000
)
