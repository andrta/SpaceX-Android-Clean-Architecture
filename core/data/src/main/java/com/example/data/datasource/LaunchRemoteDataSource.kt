package com.example.data.datasource

import com.example.domain.models.Launch

interface LaunchRemoteDataSource {
    suspend fun getLastLaunches(): List<Launch>
}
