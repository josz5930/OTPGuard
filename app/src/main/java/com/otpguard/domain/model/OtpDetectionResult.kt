package com.otpguard.domain.model

data class OtpDetectionResult(
    val isOtpDetected: Boolean,
    val matchedRuleId: Int? = null,
    val matchedRuleName: String? = null,
    val sourcePackageName: String? = null,
    val sourceAppId: Int? = null,
    val sourceChannel: String? = null,
    val timeout: Boolean = false
)
