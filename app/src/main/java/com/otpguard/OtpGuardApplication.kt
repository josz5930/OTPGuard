package com.otpguard

import android.app.Application
import android.util.Log
import com.otpguard.domain.usecase.SeedDatabaseUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class OtpGuardApplication : Application() {

    companion object {
        private const val TAG = "OtpGuardApp"
    }

    @Inject lateinit var seedDatabaseUseCase: SeedDatabaseUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate() called")
        applicationScope.launch {
            Log.d(TAG, "Calling seedDatabaseUseCase...")
            seedDatabaseUseCase.execute()
            Log.d(TAG, "seedDatabaseUseCase completed")
        }
    }
}
