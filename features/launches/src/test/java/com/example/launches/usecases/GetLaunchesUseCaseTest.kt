package com.example.launches.usecases

import com.example.domain.models.Launch
import com.example.domain.repository.LaunchRepository
import com.example.domain.result.DomainResult
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.ZonedDateTime

class GetLaunchesUseCaseTest {
    private val repository: LaunchRepository = mockk()
    private val useCase = GetLaunchesUseCase(repository)

    @Test
    fun `GIVEN unsorted list WHEN invoke THEN returns sorted list descending`() = runTest {
        // GIVEN
        val oldLaunch = mockLaunch("2023-01-01")
        val newLaunch = mockLaunch("2025-01-01")
        val midLaunch = mockLaunch("2024-01-01")

        val repoList = listOf(oldLaunch, newLaunch, midLaunch)
        every { repository.getLastLaunches(false) } returns flowOf(DomainResult.Success(repoList))

        // WHEN
        val result = useCase(false).first()

        // THEN
        assertThat(result).isInstanceOf(DomainResult.Success::class.java)
        val data = (result as DomainResult.Success).data

        assertThat(data[0].launchDate).isEqualTo(newLaunch.launchDate)
        assertThat(data[1].launchDate).isEqualTo(midLaunch.launchDate)
        assertThat(data[2].launchDate).isEqualTo(oldLaunch.launchDate)
    }

    private fun mockLaunch(date: String): Launch = mockk(relaxed = true) {
        every { launchDate } returns ZonedDateTime.parse("${date}T10:00:00Z")
    }
}
