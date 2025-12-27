package com.example.localstorage.datasource

import android.database.sqlite.SQLiteException
import com.example.data.datasource.LaunchLocalDataSource
import com.example.domain.exception.StorageException
import com.example.domain.models.Launch
import com.example.localstorage.dao.LaunchDao
import com.example.localstorage.mappers.toDomain
import com.example.localstorage.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LaunchLocalDataSourceImpl @Inject constructor(
    private val dao: LaunchDao
) : LaunchLocalDataSource {

    override fun getLaunches(): Flow<List<Launch>> {
        return dao.getAllLaunches().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getLaunchById(id: String): Launch? {
        return dao.getLaunchById(id)?.toDomain()
    }

    override suspend fun saveLaunches(launches: List<Launch>) {
        try {
            val entities = launches.map { it.toEntity() }
            dao.insertAll(entities)
        } catch (e: SQLiteException) {
            throw StorageException("Database write error", e)
        } catch (e: Exception) {
            throw StorageException("Generic storage error", e)
        }
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}
