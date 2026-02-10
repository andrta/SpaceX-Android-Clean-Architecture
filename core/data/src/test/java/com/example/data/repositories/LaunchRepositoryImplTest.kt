package com.example.data.repositories

import com.example.data.datasource.LaunchLocalDataSource
import com.example.data.datasource.LaunchRemoteDataSource
import com.example.data.repository.LaunchRepositoryImpl
import com.example.domain.featureflags.FeatureFlagProvider
import com.example.domain.models.Launch
import com.example.domain.result.DataError
import com.example.domain.result.DomainResult
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

class LaunchRepositoryImplTest {

    private lateinit var repository: LaunchRepositoryImpl
    private val graphqlSource: LaunchRemoteDataSource = mockk()
    private val restSource: LaunchRemoteDataSource = mockk()
    private val localSource: LaunchLocalDataSource = mockk(relaxed = true)
    private val featureFlags: FeatureFlagProvider = mockk()

    @Before
    fun setup() {
        repository = LaunchRepositoryImpl(
            graphqlSource = graphqlSource,
            restSource = restSource,
            localSource = localSource,
            featureFlags = featureFlags
        )
    }

    @Test
    fun `GIVEN GraphQL enabled WHEN getLastLaunches THEN calls graphql source`() = runTest {
        // GIVEN
        every { featureFlags.isGraphQlEnabled } returns true
        coEvery { graphqlSource.getLastLaunches() } returns emptyList()
        coEvery { localSource.getLaunches() } returns flowOf(emptyList())

        // WHEN
        repository.getLastLaunches(true).first()

        // THEN
        coVerify { graphqlSource.getLastLaunches() }
        coVerify(exactly = 0) { restSource.getLastLaunches() }
    }

    @Test
    fun `GIVEN GraphQL disabled WHEN getLastLaunches THEN calls rest source`() = runTest {
        // GIVEN
        every { featureFlags.isGraphQlEnabled } returns false
        coEvery { restSource.getLastLaunches() } returns emptyList()
        coEvery { localSource.getLaunches() } returns flowOf(emptyList())

        // WHEN
        repository.getLastLaunches(true).first()

        // THEN
        coVerify { restSource.getLastLaunches() }
        coVerify(exactly = 0) { graphqlSource.getLastLaunches() }
    }

    @Test
    fun `GIVEN network error AND cached data exists WHEN getLastLaunches THEN returns Success with cached data`() = runTest {
        // GIVEN
        every { featureFlags.isGraphQlEnabled } returns true
        // Network fails
        coEvery { graphqlSource.getLastLaunches() } throws IOException()

        // Cache exists
        val cachedLaunches = listOf(mockk<Launch>())
        coEvery { localSource.getLaunches() } returns flowOf(cachedLaunches)

        // WHEN
        val result = repository.getLastLaunches(false).first()

        // THEN
        // Should ignore network error and return cache
        assertThat(result).isInstanceOf(DomainResult.Success::class.java)
        assertThat((result as DomainResult.Success).data).isEqualTo(cachedLaunches)
    }

    @Test
    fun `GIVEN network error AND cache is empty WHEN getLastLaunches THEN returns Error`() = runTest {
        // GIVEN
        every { featureFlags.isGraphQlEnabled } returns true
        // Network fails
        coEvery { graphqlSource.getLastLaunches() } throws IOException()

        // Cache is empty
        coEvery { localSource.getLaunches() } returns flowOf(emptyList())

        // WHEN
        val result = repository.getLastLaunches(true).first()

        // THEN
        assertThat(result).isInstanceOf(DomainResult.Failure::class.java)
        assertThat((result as DomainResult.Failure).error).isEqualTo(DataError.Network.NoInternet)
    }

    @Test
    fun `GIVEN success network AND cache is empty WHEN getLastLaunches THEN returns Success Empty`() = runTest {
        // GIVEN
        every { featureFlags.isGraphQlEnabled } returns true
        coEvery { graphqlSource.getLastLaunches() } returns emptyList()
        coEvery { localSource.getLaunches() } returns flowOf(emptyList())

        // WHEN
        val result = repository.getLastLaunches(false).first()

        // THEN
        assertThat(result).isInstanceOf(DomainResult.Success::class.java)
        assertThat((result as DomainResult.Success).data).isEmpty()
    }
}
