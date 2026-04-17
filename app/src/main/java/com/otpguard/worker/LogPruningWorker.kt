package com.otpguard.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.otpguard.domain.repository.AppConfigRepository
import com.otpguard.domain.repository.DetectionEventRepository
import com.otpguard.util.ConfigKeys
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LogPruningWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val detectionEventRepository: DetectionEventRepository,
    private val appConfigRepository: AppConfigRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "LogPruningWorker"
        const val WORK_NAME = "log_pruning"
    }

    override suspend fun doWork(): Result {
        return try {
            val retentionDays = appConfigRepository.getLong(ConfigKeys.LOG_RETENTION_DAYS, 90L)
            val cutoffSeconds = (System.currentTimeMillis() / 1000) - (retentionDays * 86_400L)
            detectionEventRepository.deleteOlderThan(cutoffSeconds)
            Log.d(TAG, "Pruned detection events older than $retentionDays days")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to prune detection events", e)
            Result.failure()
        }
    }
}
