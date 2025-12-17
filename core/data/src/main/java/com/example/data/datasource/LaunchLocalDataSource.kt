package com.example.data.datasource

import com.example.domain.models.Launch
import kotlinx.coroutines.flow.Flow

interface LaunchLocalDataSource {
    fun getLaunches(): Flow<List<Launch>>
    suspend fun getLaunchById(id: String): Launch?
    suspend fun saveLaunches(launches: List<Launch>)
    suspend fun clearAll()
}
