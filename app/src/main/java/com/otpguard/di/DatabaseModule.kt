package com.otpguard.di

import android.content.Context
import androidx.room.Room
import com.otpguard.data.local.dao.*
import com.otpguard.data.local.database.OtpGuardDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val applicationScope = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OtpGuardDatabase {
        return OtpGuardDatabase.getDatabase(context, applicationScope)
    }

    @Provides fun provideMonitoredAppDao(db: OtpGuardDatabase): MonitoredAppDao = db.monitoredAppDao()
    @Provides fun provideRegexRuleDao(db: OtpGuardDatabase): RegexRuleDao = db.regexRuleDao()
    @Provides fun provideDetectionEventDao(db: OtpGuardDatabase): DetectionEventDao = db.detectionEventDao()
    @Provides fun provideAppConfigDao(db: OtpGuardDatabase): AppConfigDao = db.appConfigDao()
    @Provides fun provideWarningTemplateDao(db: OtpGuardDatabase): WarningTemplateDao = db.warningTemplateDao()
    @Provides fun provideInputValidationRuleDao(db: OtpGuardDatabase): InputValidationRuleDao = db.inputValidationRuleDao()
}
