package com.otpguard.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DetectionEventWithDetails(
    @Embedded val event: DetectionEventEntity,
    @Relation(parentColumn = "app_id", entityColumn = "id")
    val app: MonitoredAppEntity?,
    @Relation(parentColumn = "rule_id", entityColumn = "id")
    val rule: RegexRuleEntity?,
    @Relation(parentColumn = "template_id", entityColumn = "id")
    val template: WarningTemplateEntity?
)
