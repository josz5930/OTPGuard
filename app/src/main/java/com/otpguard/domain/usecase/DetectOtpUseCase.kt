package com.otpguard.domain.usecase

import android.util.Log
import com.otpguard.domain.model.OtpDetectionResult
import com.otpguard.domain.repository.AppConfigRepository
import com.otpguard.domain.repository.MonitoredAppRepository
import com.otpguard.domain.repository.RegexRuleRepository
import com.otpguard.util.ConfigKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetectOtpUseCase @Inject constructor(
    private val monitoredAppRepository: MonitoredAppRepository,
    private val regexRuleRepository: RegexRuleRepository,
    private val appConfigRepository: AppConfigRepository
) {
    companion object {
        private const val TAG = "DetectOtpUseCase"
    }

    suspend fun execute(packageName: String, notificationText: String): OtpDetectionResult {
        Log.d(TAG, "execute() called with packageName=$packageName")

        val app = monitoredAppRepository.getByPackageName(packageName)
        if (app == null) {
            Log.d(TAG, "App not in monitored list: $packageName")
            return OtpDetectionResult(isOtpDetected = false, timeout = false)
        }

        Log.d(TAG, "Found monitored app: ${app.displayName}, enabled=${app.isEnabled}")

        if (!app.isEnabled) {
            Log.d(TAG, "App is disabled")
            return OtpDetectionResult(isOtpDetected = false, timeout = false)
        }

        val rules = regexRuleRepository.getEnabledRules()
        Log.d(TAG, "Found ${rules.size} enabled rules")

        val timeoutMs = appConfigRepository.get(ConfigKeys.REGEX_TIMEOUT_MS)?.toIntOrNull() ?: 200
        var timedOut = false

        for (rule in rules) {
            try {
                val isMatch = withContext(Dispatchers.Default) {
                    withTimeout(timeoutMs.toLong()) {
                        val regex = Regex(rule.pattern)
                        regex.containsMatchIn(notificationText)
                    }
                }

                Log.d(TAG, "Rule '${rule.name}' match result: $isMatch")

                if (isMatch) {
                    Log.d(TAG, "OTP DETECTED! Rule: ${rule.name}")
                    return OtpDetectionResult(
                        isOtpDetected = true,
                        matchedRuleId = rule.id,
                        matchedRuleName = rule.name,
                        sourcePackageName = packageName,
                        sourceAppId = app.id,
                        sourceChannel = app.channel,
                        timeout = timedOut
                    )
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                timedOut = true
                continue
            } catch (e: Exception) {
                Log.e(TAG, "Error matching rule '${rule.name}': ${e.message}")
                continue
            }
        }

        Log.d(TAG, "No rules matched")
        return OtpDetectionResult(isOtpDetected = false, timeout = timedOut)
    }
}
