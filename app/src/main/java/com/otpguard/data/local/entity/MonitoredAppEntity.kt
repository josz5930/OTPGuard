package com.otpguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "monitored_app",
    indices = [Index(value = ["package_name"], unique = true)]
)
data class MonitoredAppEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "channel") val channel: String = "other",
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = true,
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis() / 1000,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis() / 1000
)
