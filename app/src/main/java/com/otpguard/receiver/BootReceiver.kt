package com.otpguard.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("OtpGuard", "Device booted — NotificationListenerService will auto-restart if enabled")
            // NotificationListenerService is automatically restarted by the system
            // when the user has granted notification access permission.
            // No manual start needed.
        }
    }
}
