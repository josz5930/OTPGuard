package com.otpguard.domain.repository

import com.otpguard.data.local.entity.AppConfigEntity
import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {
    suspend fun get(key: String): String?
    fun observe(key: String): Flow<AppConfigEntity?>
    fun getAll(): Flow<List<AppConfigEntity>>
    suspend fun set(key: String, value: String)
    suspend fun getBoolean(key: String, default: Boolean): Boolean
    suspend fun getInt(key: String, default: Int): Int
    suspend fun getLong(key: String, default: Long): Long
}
