package com.example.launches.usecases

import app.cash.turbine.test
import com.example.domain.models.Launch
import com.example.domain.repository.LaunchRepository
import com.example.domain.result.DataError
import com.example.domain.result.DomainResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class GetLaunchDetailsUseCaseTest {

    private val repository: LaunchRepository = mockk()
    private val useCase = GetLaunchDetailsUseCase(repository)

    @Test
    fun `invoke calls repository and returns success`() = runTest {
        // GIVEN
        val launchId = "123"
        val mockLaunch = mockk<Launch>()
        every { repository.getLaunchDetails(launchId) } returns flowOf(DomainResult.Success(mockLaunch))

        // WHEN
        val resultFlow = useCase(launchId)

        // THEN
        resultFlow.test {
            val item = awaitItem()
            assertThat(item).isInstanceOf(DomainResult.Success::class.java)
            assertThat((item as DomainResult.Success).data).isEqualTo(mockLaunch)
            awaitComplete()
        }

        verify(exactly = 1) { repository.getLaunchDetails(launchId) }
    }

    @Test
    fun `invoke calls repository and returns failure`() = runTest {
        // GIVEN
        val launchId = "999"
        val error = DataError.Local.DiskRead
        every { repository.getLaunchDetails(launchId) } returns flowOf(DomainResult.Failure(error))

        // WHEN
        val resultFlow = useCase(launchId)

        // THEN
        resultFlow.test {
            val item = awaitItem()
            assertThat(item).isInstanceOf(DomainResult.Failure::class.java)
            assertThat((item as DomainResult.Failure).error).isEqualTo(error)
            awaitComplete()
        }
    }
}
