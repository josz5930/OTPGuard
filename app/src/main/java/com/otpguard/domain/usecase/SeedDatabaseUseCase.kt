package com.otpguard.domain.usecase

import android.util.Log
import com.otpguard.data.local.dao.InputValidationRuleDao
import com.otpguard.data.local.dao.WarningTemplateDao
import com.otpguard.data.local.database.DatabaseSeeder
import com.otpguard.domain.repository.AppConfigRepository
import com.otpguard.domain.repository.MonitoredAppRepository
import com.otpguard.domain.repository.RegexRuleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDatabaseUseCase @Inject constructor(
    private val monitoredAppRepository: MonitoredAppRepository,
    private val regexRuleRepository: RegexRuleRepository,
    private val appConfigRepository: AppConfigRepository,
    private val warningTemplateDao: WarningTemplateDao,
    private val inputValidationRuleDao: InputValidationRuleDao
) {
    companion object {
        private const val TAG = "SeedDatabaseUseCase"
    }

    suspend fun execute() {
        Log.d(TAG, "execute() called - checking if seeding needed")

        val appCount = monitoredAppRepository.count()
        val ruleCount = regexRuleRepository.count()
        Log.d(TAG, "Current counts: apps=$appCount, rules=$ruleCount")

        if (appCount == 0) {
            Log.d(TAG, "Seeding monitored apps...")
            monitoredAppRepository.insertAll(DatabaseSeeder.getDefaultMonitoredApps())
            Log.d(TAG, "Seeded ${DatabaseSeeder.getDefaultMonitoredApps().size} monitored apps")
        }

        if (ruleCount == 0) {
            Log.d(TAG, "Seeding regex rules...")
            regexRuleRepository.insertAll(DatabaseSeeder.getDefaultRegexRules())
            Log.d(TAG, "Seeded ${DatabaseSeeder.getDefaultRegexRules().size} regex rules")
        }

        if (appConfigRepository.get("service_enabled") == null) {
            Log.d(TAG, "Seeding config values...")
            DatabaseSeeder.getDefaultConfigs().forEach {
                appConfigRepository.set(it.key, it.value)
            }
        }

        if (warningTemplateDao.count() == 0) {
            Log.d(TAG, "Seeding warning template...")
            warningTemplateDao.insert(DatabaseSeeder.getDefaultWarningTemplate())
        }

        if (inputValidationRuleDao.count() == 0) {
            Log.d(TAG, "Seeding input validation rules...")
            inputValidationRuleDao.insertAll(DatabaseSeeder.getDefaultInputValidationRules())
        }

        // Log final state
        val finalAppCount = monitoredAppRepository.count()
        val finalRuleCount = regexRuleRepository.count()
        Log.d(TAG, "Final counts: apps=$finalAppCount, rules=$finalRuleCount")

        // List all monitored apps for debugging
        val apps = monitoredAppRepository.getEnabledApps()
        Log.d(TAG, "Monitored apps (${apps.size}):")
        apps.forEach { app ->
            Log.d(TAG, "  - ${app.packageName} (${app.displayName})")
        }
    }
}
