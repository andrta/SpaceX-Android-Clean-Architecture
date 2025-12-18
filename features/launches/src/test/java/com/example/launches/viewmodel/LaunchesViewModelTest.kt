package com.example.launches.viewmodel

import app.cash.turbine.test
import com.example.domain.models.Launch
import com.example.domain.result.DataError
import com.example.domain.result.DomainResult
import com.example.launches.model.LaunchesUiState
import com.example.launches.usecases.GetLaunchesUseCase
import com.example.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LaunchesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getLaunchesUseCase: GetLaunchesUseCase = mockk()

    private lateinit var viewModel: LaunchesViewModel

    @Test
    fun `GIVEN UseCase returns success WHEN init THEN state is Success`() = runTest {
        // GIVEN
        val mockLaunches = listOf(mockk<Launch>())
        coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
            DomainResult.Success(
                mockLaunches
            )
        )

        // WHEN
        viewModel = LaunchesViewModel(getLaunchesUseCase)

        // THEN
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(LaunchesUiState.Loading)

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(LaunchesUiState.Success::class.java)
            assertThat((successState as LaunchesUiState.Success).launches).isEqualTo(mockLaunches)
        }
    }

    @Test
    fun `GIVEN UseCase returns failure WHEN init THEN state is Error`() = runTest {
        // GIVEN
        coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
            DomainResult.Failure(
                DataError.Network
            )
        )

        // WHEN
        viewModel = LaunchesViewModel(getLaunchesUseCase)

        // THEN
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(LaunchesUiState.Loading)

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LaunchesUiState.Error::class.java)
            assertThat((errorState as LaunchesUiState.Error).error).isEqualTo(DataError.Network)
        }
    }

    @Test
    fun `GIVEN existing data WHEN onRefresh succeed THEN show new data in Success state`() =
        runTest {
            // GIVEN
            val newData = listOf(mockk<Launch>())
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(
                    newData
                )
            )

            viewModel = LaunchesViewModel(getLaunchesUseCase)

            // Consume the initial emissions
            viewModel.uiState.test {
                awaitItem() // Loading
                awaitItem() // Success

                // Prepare for Refresh Success
                coEvery { getLaunchesUseCase(forceRefresh = true) } returns flowOf(
                    DomainResult.Success(
                        newData
                    )
                )

                // WHEN
                viewModel.onRefresh()

                // THEN
                val result = awaitItem()
                assertThat(result).isInstanceOf(LaunchesUiState.Success::class.java)

                val state = result as LaunchesUiState.Success
                assertThat(state.launches).isEqualTo(newData)
            }
        }

    @Test
    fun `GIVEN existing data WHEN onRefresh fails THEN preserves old data in Error state`() =
        runTest {
            // GIVEN
            val oldData = listOf(mockk<Launch>())
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(
                    oldData
                )
            )

            viewModel = LaunchesViewModel(getLaunchesUseCase)

            // Consume the initial emissions
            viewModel.uiState.test {
                awaitItem() // Loading
                awaitItem() // Success

                // Prepare for Refresh Failure
                coEvery { getLaunchesUseCase(forceRefresh = true) } returns flowOf(
                    DomainResult.Failure(
                        DataError.Server
                    )
                )

                // WHEN
                viewModel.onRefresh()

                // THEN
                val errorState = awaitItem()
                assertThat(errorState).isInstanceOf(LaunchesUiState.Error::class.java)

                val state = errorState as LaunchesUiState.Error
                // Verify we are showing the specific error
                assertThat(state.error).isEqualTo(DataError.Server)
                // Verify we are still holding the old data (Good UX)
                assertThat(state.launches).isEqualTo(oldData)
            }
        }
}
