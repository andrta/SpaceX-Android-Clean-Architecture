package com.example.domain.repository

import com.example.domain.models.Launch
import com.example.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow

interface LaunchRepository {
    fun getLaunches(forceRefresh: Boolean = false): Flow<DomainResult<List<Launch>>>
    fun getLaunchById(id: String): Flow<DomainResult<Launch>>
}
