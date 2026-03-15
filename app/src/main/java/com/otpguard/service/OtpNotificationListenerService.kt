package com.otpguard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.otpguard.domain.usecase.DetectOtpUseCase
import com.otpguard.domain.usecase.LogDetectionEventUseCase
import com.otpguard.domain.usecase.ResolveTemplateUseCase
import com.otpguard.domain.repository.AppConfigRepository
import com.otpguard.domain.repository.MonitoredAppRepository
import com.otpguard.ui.MainActivity
import com.otpguard.util.ConfigKeys
import com.otpguard.util.NotificationConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OtpNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG = "OtpListener"
    }

    @Inject lateinit var detectOtpUseCase: DetectOtpUseCase
    @Inject lateinit var logDetectionEventUseCase: LogDetectionEventUseCase
    @Inject lateinit var resolveTemplateUseCase: ResolveTemplateUseCase
    @Inject lateinit var appConfigRepository: AppConfigRepository
    @Inject lateinit var monitoredAppRepository: MonitoredAppRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val handler = Handler(Looper.getMainLooper())
    private var lastWarningTime = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        Log.d(TAG, "=== SERVICE CREATED ===")
        Log.d(TAG, "Notification Listener Service is now running!")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "=== LISTENER CONNECTED ===")
        Log.d(TAG, "Successfully connected to notification system")

        // On API 35+, request rebind to get full notification access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                requestRebind(android.content.ComponentName(this, this::class.java))
                Log.d(TAG, "Requested rebind for full notification access")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request rebind: ${e.message}")
            }
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "=== LISTENER DISCONNECTED ===")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        Log.d(TAG, "=== NOTIFICATION RECEIVED ===")
        Log.d(TAG, "Package: ${sbn.packageName}")

        if (sbn.packageName == packageName) {
            Log.d(TAG, "Ignoring own notification")
            return
        }

        val extras = sbn.notification.extras
        if (extras == null) {
            Log.d(TAG, "No extras in notification")
            return
        }

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        // Try additional text fields that might contain content
        val textLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString(" ") ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString() ?: ""
        val infoText = extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString() ?: ""
        val summaryText = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString() ?: ""

        val combinedText = listOf(title, text, textLines, bigText, subText, infoText, summaryText)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .trim()

        Log.d(TAG, "Title: $title")
        Log.d(TAG, "Text: $text")
        Log.d(TAG, "Combined: $combinedText")

        // Check for hidden notification content (API 35+ privacy feature)
        if (combinedText.contains("Sensitive notification content hidden", ignoreCase = true) ||
            combinedText.contains("content hidden", ignoreCase = true)) {
            Log.w(TAG, "=== NOTIFICATION CONTENT HIDDEN ===")
            Log.w(TAG, "This is an Android 15+ privacy feature. The app may need to be re-authorized.")
            Log.w(TAG, "Please go to Settings > Apps > OTP Guard > Notification access and toggle it off/on.")
            // Still continue processing - maybe there's still something in the title
        }

        if (combinedText.isBlank()) {
            Log.d(TAG, "Combined text is blank, returning")
            return
        }

        serviceScope.launch {
            try {
                val serviceEnabled = appConfigRepository.getBoolean(ConfigKeys.SERVICE_ENABLED, true)
                Log.d(TAG, "Service enabled: $serviceEnabled")
                if (!serviceEnabled) {
                    Log.d(TAG, "Service disabled, returning")
                    return@launch
                }

                Log.d(TAG, "Calling detectOtpUseCase...")
                val result = detectOtpUseCase.execute(sbn.packageName, combinedText)
                Log.d(TAG, "Detection result: isOtp=${result.isOtpDetected}, appId=${result.sourceAppId}, ruleId=${result.matchedRuleId}")

                if (result.isOtpDetected && result.sourceAppId != null && result.matchedRuleId != null) {
                    val collapseWindowMs = appConfigRepository.getLong(ConfigKeys.COLLAPSE_WINDOW_MS, 5000L)
                    val now = System.currentTimeMillis()
                    val shouldCollapse = (now - lastWarningTime) < collapseWindowMs

                    val app = monitoredAppRepository.getById(result.sourceAppId)
                    var templateId: Int? = null

                    if (!shouldCollapse && app != null) {
                        val template = resolveTemplateUseCase.execute(app)
                        templateId = template.id
                        lastWarningTime = now
                        val rendered = resolveTemplateUseCase.renderTemplate(
                            template = template,
                            appName = app.displayName,
                            channel = app.channel,
                            timestamp = now
                        )
                        Log.d(TAG, "Posting warning notification: ${rendered.title}")
                        postWarningNotification(rendered.title, rendered.body)
                    }

                    logDetectionEventUseCase.execute(
                        appId = result.sourceAppId,
                        ruleId = result.matchedRuleId,
                        templateId = templateId,
                        notificationKey = sbn.key,
                        warningPosted = !shouldCollapse,
                        timeout = result.timeout
                    )

                    Log.d(TAG, "OTP detected from ${sbn.packageName}, rule: ${result.matchedRuleName}")
                } else if (!result.isOtpDetected) {
                    Log.d(TAG, "No OTP detected for this notification")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        // High-priority warning channel with sound and vibration
        val warningChannel = NotificationChannel(
            NotificationConstants.CHANNEL_ID_WARNING,
            NotificationConstants.CHANNEL_NAME_WARNING,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Critical warnings when OTP codes are detected"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
            enableLights(true)
            lightColor = Color.RED
            setShowBadge(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
        }

        val serviceChannel = NotificationChannel(
            NotificationConstants.CHANNEL_ID_SERVICE,
            NotificationConstants.CHANNEL_NAME_SERVICE,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "OTP Guard background service"
            setShowBadge(false)
        }

        manager.createNotificationChannel(warningChannel)
        manager.createNotificationChannel(serviceChannel)
    }

    private suspend fun postWarningNotification(title: String, body: String) {
        Log.d(TAG, "postWarningNotification() called with title=$title")

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "detection_log")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundEnabled = appConfigRepository.getBoolean(ConfigKeys.WARNING_SOUND_ENABLED, true)
        val vibrateEnabled = appConfigRepository.getBoolean(ConfigKeys.WARNING_VIBRATE_ENABLED, true)

        // Build a high-priority notification using NotificationCompat
        val notification = NotificationCompat.Builder(this, NotificationConstants.CHANNEL_ID_WARNING)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOnlyAlertOnce(false)
            .setLocalOnly(false)
            .setTimeoutAfter(60_000) // Auto-dismiss after 60 seconds
            .apply {
                if (vibrateEnabled) {
                    setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
                }
                if (soundEnabled) {
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                }
            }
            .build()

        val manager = getSystemService(NotificationManager::class.java)

        Log.d(TAG, "Notifying with ID: ${NotificationConstants.WARNING_NOTIFICATION_ID}")
        manager.notify(NotificationConstants.WARNING_NOTIFICATION_ID, notification)

        // Also cancel any existing notification first to ensure heads-up shows
        manager.cancel(NotificationConstants.WARNING_NOTIFICATION_ID)
        manager.notify(NotificationConstants.WARNING_NOTIFICATION_ID, notification)

        Log.d(TAG, "Warning notification posted successfully")
    }
}
