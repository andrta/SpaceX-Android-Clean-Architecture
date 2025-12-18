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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LaunchesViewModelTest {
    private val testDispatcher =
        StandardTestDispatcher() // 1. Create a dispatcher that allows us to control virtual time

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule(testDispatcher) // 2. Pass it to the Rule (which will replace the Main Thread)

    private val getLaunchesUseCase: GetLaunchesUseCase = mockk()
    private lateinit var viewModel: LaunchesViewModel

    @Test
    fun `GIVEN UseCase returns success WHEN init THEN state is Success`() =
        runTest(testDispatcher) {
            // GIVEN
            val mockLaunches = listOf(mockk<Launch>())
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(
                    mockLaunches
                )
            )

            // WHEN
            // The ViewModel is initialized. The coroutine in the init block is launched BUT PAUSED (queued).
            viewModel = LaunchesViewModel(getLaunchesUseCase)

            // THEN
            viewModel.uiState.test {
                // 1. We can now verify the initial Loading state
                assertThat(awaitItem()).isEqualTo(LaunchesUiState.Loading)

                // 2. Advance time to execute the queued loading coroutine
                advanceUntilIdle()

                // 3. The Success state arrives
                val successState = awaitItem()
                assertThat(successState).isInstanceOf(LaunchesUiState.Success::class.java)
                assertThat((successState as LaunchesUiState.Success).launches).isEqualTo(
                    mockLaunches
                )
            }
        }

    @Test
    fun `GIVEN UseCase returns failure WHEN init THEN state is Error`() = runTest(testDispatcher) {
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
            assertThat(awaitItem()).isEqualTo(LaunchesUiState.Loading) // Verify loading

            advanceUntilIdle() // Execute pending work

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LaunchesUiState.Error::class.java)
            assertThat((errorState as LaunchesUiState.Error).error).isEqualTo(DataError.Network)
        }
    }

    @Test
    fun `GIVEN existing data WHEN onRefresh succeed THEN show new data in Success state`() =
        runTest(testDispatcher) {
            // GIVEN - Initial load
            val initialData = listOf(mockk<Launch>()) // 1. Create specific initial data
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(
                    initialData
                )
            )

            viewModel = LaunchesViewModel(getLaunchesUseCase)
            advanceUntilIdle() // Complete init

            // PREPARE REFRESH
            val refreshedData = listOf(mockk<Launch>()) // 2. Create DIFFERENT data for refresh
            coEvery { getLaunchesUseCase(forceRefresh = true) } returns flowOf(
                DomainResult.Success(
                    refreshedData
                )
            )

            viewModel.uiState.test {
                // Consume the initial state (Success with initialData)
                val initialState = awaitItem()
                assertThat(initialState).isInstanceOf(LaunchesUiState.Success::class.java)

                // WHEN
                viewModel.onRefresh()
                advanceUntilIdle() // Execute refresh

                // THEN
                val result =
                    awaitItem() // 3. Now StateFlow WILL emit because refreshedData != initialData
                assertThat(result).isInstanceOf(LaunchesUiState.Success::class.java)
                assertThat((result as LaunchesUiState.Success).launches).isEqualTo(refreshedData)
            }
        }

    @Test
    fun `GIVEN existing data WHEN onRefresh fails THEN preserves old data in Error state`() =
        runTest(testDispatcher) {
            // GIVEN - Initial load
            val oldData = listOf(mockk<Launch>())
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(
                    oldData
                )
            )

            viewModel = LaunchesViewModel(getLaunchesUseCase)
            advanceUntilIdle() // Complete init

            // PREPARE REFRESH FAIL
            coEvery { getLaunchesUseCase(forceRefresh = true) } returns flowOf(
                DomainResult.Failure(
                    DataError.Server
                )
            )

            viewModel.uiState.test {
                awaitItem()  // Consume initial state

                // WHEN
                viewModel.onRefresh()
                advanceUntilIdle() // Execute the failing refresh

                // THEN
                val errorState = awaitItem()
                assertThat(errorState).isInstanceOf(LaunchesUiState.Error::class.java)

                val state = errorState as LaunchesUiState.Error
                assertThat(state.error).isEqualTo(DataError.Server)
                // UX Verification: old data is still preserved
                assertThat(state.launches).isEqualTo(oldData)
            }
        }
}
