package com.otpguard.domain.usecase

import com.otpguard.data.local.entity.DetectionEventEntity
import com.otpguard.domain.repository.AppConfigRepository
import com.otpguard.domain.repository.DetectionEventRepository
import com.otpguard.util.EventTypes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogDetectionEventUseCase @Inject constructor(
    private val detectionEventRepository: DetectionEventRepository,
    private val appConfigRepository: AppConfigRepository
) {
    suspend fun execute(
        appId: Int?,
        ruleId: Int?,
        templateId: Int? = null,
        notificationKey: String?,
        warningPosted: Boolean,
        timeout: Boolean = false
    ): Long {
        val event = DetectionEventEntity(
            eventType = EventTypes.DETECTION,
            appId = appId,
            ruleId = ruleId,
            templateId = templateId,
            notificationKey = notificationKey,
            warningPosted = warningPosted,
            timeout = timeout
        )
        return detectionEventRepository.insert(event)
    }

    suspend fun logServiceToggle(newState: String): Long {
        val event = DetectionEventEntity(
            eventType = EventTypes.SERVICE_TOGGLE,
            appId = null,
            ruleId = null,
            templateId = null,
            newServiceState = newState,
            warningPosted = false,
            timeout = false
        )
        return detectionEventRepository.insert(event)
    }

    suspend fun shouldCollapseWarning(notificationKey: String?): Boolean {
        if (notificationKey == null) return false
        val collapseWindowMs = appConfigRepository.getLong("collapse_window_ms", 5000L)
        val since = (System.currentTimeMillis() / 1000) - (collapseWindowMs / 1000)
        val recentKeys = detectionEventRepository.getRecentNotificationKeys(since)
        return recentKeys.any { it == notificationKey }
    }
}
