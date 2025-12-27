package com.example.data.services

import com.example.data.models.dto.LaunchDto
import com.example.data.models.dto.RocketDto
import retrofit2.http.GET
import retrofit2.http.Path

interface SpaceXApiService {
    @GET("launches")
    suspend fun getLaunches(): List<LaunchDto>

    @GET("rockets/{id}")
    suspend fun getRocket(@Path("id") id: String): RocketDto
}
