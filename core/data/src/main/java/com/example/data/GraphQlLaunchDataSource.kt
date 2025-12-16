package com.example.data

import com.apollographql.apollo3.ApolloClient
import com.example.data.mappers.toDomain
import com.example.domain.models.Launch
import javax.inject.Inject

class GraphQlLaunchDataSource @Inject constructor(
    private val apolloClient: ApolloClient
) : LaunchRemoteDataSource {
    override suspend fun getLastLaunches(): List<Launch> {
        val response = apolloClient.query(GetPastLaunchesQuery()).execute()
        return response.data?.launchesPast
            ?.filterNotNull()
            ?.map { it.toDomain() }
            ?: emptyList()
    }
}
