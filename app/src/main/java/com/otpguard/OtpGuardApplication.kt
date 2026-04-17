package com.otpguard

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.otpguard.domain.usecase.SeedDatabaseUseCase
import com.otpguard.worker.LogPruningWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class OtpGuardApplication : Application(), Configuration.Provider {

    companion object {
        private const val TAG = "OtpGuardApp"
    }

    @Inject lateinit var seedDatabaseUseCase: SeedDatabaseUseCase
    @Inject lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate() called")
        applicationScope.launch {
            Log.d(TAG, "Calling seedDatabaseUseCase...")
            seedDatabaseUseCase.execute()
            Log.d(TAG, "seedDatabaseUseCase completed")
        }
        scheduleLogPruning()
    }

    private fun scheduleLogPruning() {
        val request = PeriodicWorkRequestBuilder<LogPruningWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            LogPruningWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
