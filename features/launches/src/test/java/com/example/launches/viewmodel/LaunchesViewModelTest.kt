package com.example.launches.viewmodel

import app.cash.turbine.test
import com.example.domain.models.Launch
import com.example.domain.result.DataError
import com.example.domain.result.DomainResult
import com.example.launches.model.LaunchesIntent
import com.example.launches.model.LaunchesUiEffect
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

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val getLaunchesUseCase: GetLaunchesUseCase = mockk()
    private lateinit var viewModel: LaunchesViewModel

    @Test
    fun `GIVEN UseCase returns success WHEN init THEN state is Success`() = runTest(testDispatcher) {
        // GIVEN
        val mockLaunches = listOf(mockk<Launch>())
        coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(DomainResult.Success(mockLaunches))

        // WHEN
        viewModel = LaunchesViewModel(getLaunchesUseCase)

        // THEN
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(LaunchesUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(LaunchesUiState.Success::class.java)
            assertThat((successState as LaunchesUiState.Success).launches).isEqualTo(mockLaunches)
        }
    }

    @Test
    fun `GIVEN UseCase returns failure WHEN init THEN state is Error`() = runTest(testDispatcher) {
        // GIVEN
        coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(DomainResult.Failure(DataError.Network))

        // WHEN
        viewModel = LaunchesViewModel(getLaunchesUseCase)

        // THEN
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(LaunchesUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LaunchesUiState.Error::class.java)
            assertThat((errorState as LaunchesUiState.Error).error).isEqualTo(DataError.Network)
        }
    }

    @Test
    fun `GIVEN existing data WHEN Refresh Intent succeed THEN show new data`() =
        runTest(testDispatcher) {
            // GIVEN - Initial load
            val initialData = listOf(mockk<Launch>())
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(DomainResult.Success(initialData))

            viewModel = LaunchesViewModel(getLaunchesUseCase)
            advanceUntilIdle()

            // PREPARE REFRESH
            val refreshedData = listOf(mockk<Launch>()) // Different object
            coEvery { getLaunchesUseCase(forceRefresh = true) } returns flowOf(DomainResult.Success(refreshedData))

            viewModel.uiState.test {
                val initialState = awaitItem() // Consume initial state
                assertThat(initialState).isInstanceOf(LaunchesUiState.Success::class.java)

                // WHEN - MVI Style
                viewModel.process(LaunchesIntent.Refresh)
                advanceUntilIdle()

                // THEN
                val result = awaitItem()
                assertThat(result).isInstanceOf(LaunchesUiState.Success::class.java)
                assertThat((result as LaunchesUiState.Success).launches).isEqualTo(refreshedData)
            }
        }

    @Test
    fun `GIVEN existing data WHEN Refresh Intent fails THEN preserves old data`() =
        runTest(testDispatcher) {
            // GIVEN - Initial load
            val oldData = listOf(mockk<Launch>())
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(DomainResult.Success(oldData))

            viewModel = LaunchesViewModel(getLaunchesUseCase)
            advanceUntilIdle()

            // PREPARE REFRESH FAIL
            coEvery { getLaunchesUseCase(forceRefresh = true) } returns flowOf(DomainResult.Failure(DataError.Server))

            viewModel.uiState.test {
                awaitItem() // Consume initial state

                // WHEN - MVI Style
                viewModel.process(LaunchesIntent.Refresh)
                advanceUntilIdle()

                // THEN
                val errorState = awaitItem()
                assertThat(errorState).isInstanceOf(LaunchesUiState.Error::class.java)
                val state = errorState as LaunchesUiState.Error

                assertThat(state.error).isEqualTo(DataError.Server)
                // Verify old data is preserved
                assertThat(state.launches).isEqualTo(oldData)
            }
        }

    // --- NEW TEST FOR SIDE EFFECTS ---
    @Test
    fun `GIVEN LaunchClicked Intent WHEN process THEN emits NavigateToDetail Effect`() =
        runTest(testDispatcher) {
            // GIVEN
            val launchId = "123"
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(DomainResult.Success(emptyList()))

            viewModel = LaunchesViewModel(getLaunchesUseCase)
            advanceUntilIdle()

            // Test the Side Effect Channel
            viewModel.uiEffect.test {
                // WHEN
                viewModel.process(LaunchesIntent.LaunchClicked(launchId))

                // THEN
                val effect = awaitItem()
                assertThat(effect).isInstanceOf(LaunchesUiEffect.NavigateToDetail::class.java)
                assertThat((effect as LaunchesUiEffect.NavigateToDetail).launchId).isEqualTo(launchId)
            }
        }
}