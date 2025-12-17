package com.example.localstorage.datasource


import com.example.data.datasource.LaunchLocalDataSource
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
        dao.insertAll(launches.map { it.toEntity() })
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}
