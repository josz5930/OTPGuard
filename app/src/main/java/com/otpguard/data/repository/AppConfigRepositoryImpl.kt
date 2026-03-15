package com.otpguard.data.repository

import com.otpguard.data.local.dao.AppConfigDao
import com.otpguard.data.local.entity.AppConfigEntity
import com.otpguard.domain.repository.AppConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfigRepositoryImpl @Inject constructor(
    private val dao: AppConfigDao
) : AppConfigRepository {

    override suspend fun get(key: String): String? = dao.get(key)?.value

    override fun observe(key: String): Flow<AppConfigEntity?> = dao.observe(key)

    override fun getAll(): Flow<List<AppConfigEntity>> = dao.getAll()

    override suspend fun set(key: String, value: String) {
        dao.set(AppConfigEntity(key = key, value = value))
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return dao.get(key)?.value?.toBooleanStrictOrNull() ?: default
    }

    override suspend fun getInt(key: String, default: Int): Int {
        return dao.get(key)?.value?.toIntOrNull() ?: default
    }

    override suspend fun getLong(key: String, default: Long): Long {
        return dao.get(key)?.value?.toLongOrNull() ?: default
    }
}
