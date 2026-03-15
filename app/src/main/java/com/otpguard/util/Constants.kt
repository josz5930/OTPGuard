package com.otpguard.util

object NotificationConstants {
    const val CHANNEL_ID_WARNING = "otp_guard_warning"
    const val CHANNEL_NAME_WARNING = "OTP Warning"
    const val CHANNEL_ID_SERVICE = "otp_guard_service"
    const val CHANNEL_NAME_SERVICE = "OTP Guard Service"
    const val WARNING_NOTIFICATION_ID = 1001
    const val SERVICE_NOTIFICATION_ID = 1002
}

object ConfigKeys {
    const val WARNING_SOUND_ENABLED = "warning_sound_enabled"
    const val WARNING_VIBRATE_ENABLED = "warning_vibrate_enabled"
    const val WARNING_AUTO_DISMISS_SECONDS = "warning_auto_dismiss_seconds"
    const val COLLAPSE_WINDOW_MS = "collapse_window_ms"
    const val SERVICE_ENABLED = "service_enabled"
    const val NOTIFICATION_ACCESS_GRANTED = "notification_access_granted"
    const val LOG_RETENTION_DAYS = "log_retention_days"
    const val REGEX_TIMEOUT_MS = "regex_timeout_ms"
}

object TemplatePlaceholders {
    const val APP_NAME = "{{APP_NAME}}"
    const val CHANNEL = "{{CHANNEL}}"
    const val TIMESTAMP = "{{TIMESTAMP}}"
}

object TemplateScopes {
    const val GLOBAL = "global"
    const val CHANNEL = "channel"
    const val APP = "app"
}

object Channels {
    const val WHATSAPP = "whatsapp"
    const val SMS = "sms"
    const val OTHER = "other"
}

object EventTypes {
    const val DETECTION = "detection"
    const val SERVICE_TOGGLE = "service_toggle"
}

object ServiceStates {
    const val ENABLED = "enabled"
    const val DISABLED = "disabled"
}
