package com.otpguard.util

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

object NotificationAccessHelper {

    fun isNotificationAccessEnabled(context: Context): Boolean {
        val componentName = ComponentName(context, "com.otpguard.service.OtpNotificationListenerService")
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return enabledListeners.contains(componentName.flattenToString())
    }
}
