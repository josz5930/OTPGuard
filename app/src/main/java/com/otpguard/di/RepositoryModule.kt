package com.otpguard.di

import com.otpguard.data.repository.*
import com.otpguard.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMonitoredAppRepository(impl: MonitoredAppRepositoryImpl): MonitoredAppRepository

    @Binds
    @Singleton
    abstract fun bindRegexRuleRepository(impl: RegexRuleRepositoryImpl): RegexRuleRepository

    @Binds
    @Singleton
    abstract fun bindDetectionEventRepository(impl: DetectionEventRepositoryImpl): DetectionEventRepository

    @Binds
    @Singleton
    abstract fun bindAppConfigRepository(impl: AppConfigRepositoryImpl): AppConfigRepository

    @Binds
    @Singleton
    abstract fun bindWarningTemplateRepository(impl: WarningTemplateRepositoryImpl): WarningTemplateRepository

    @Binds
    @Singleton
    abstract fun bindInputValidationRuleRepository(impl: InputValidationRuleRepositoryImpl): InputValidationRuleRepository
}
