package com.otpguard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detection_event",
    foreignKeys = [
        ForeignKey(
            entity = MonitoredAppEntity::class,
            parentColumns = ["id"],
            childColumns = ["app_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = RegexRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["rule_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = WarningTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["template_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["app_id"]),
        Index(value = ["rule_id"]),
        Index(value = ["template_id"]),
        Index(value = ["detected_at"]),
        Index(value = ["event_type"])
    ]
)
data class DetectionEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "event_type") val eventType: String = "detection",
    @ColumnInfo(name = "app_id") val appId: Int? = null,
    @ColumnInfo(name = "rule_id") val ruleId: Int? = null,
    @ColumnInfo(name = "detected_at") val detectedAt: Long = System.currentTimeMillis() / 1000,
    @ColumnInfo(name = "notification_key") val notificationKey: String? = null,
    @ColumnInfo(name = "warning_posted") val warningPosted: Boolean = true,
    @ColumnInfo(name = "timeout") val timeout: Boolean = false,
    @ColumnInfo(name = "new_service_state") val newServiceState: String? = null,
    @ColumnInfo(name = "template_id") val templateId: Int? = null,
    @ColumnInfo(name = "row_hash") val rowHash: String? = null
)
