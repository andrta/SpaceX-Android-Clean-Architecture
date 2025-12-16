package com.example.data

import com.example.domain.models.Launch

interface LaunchRemoteDataSource {
    suspend fun getLastLaunches(): List<Launch>
}
