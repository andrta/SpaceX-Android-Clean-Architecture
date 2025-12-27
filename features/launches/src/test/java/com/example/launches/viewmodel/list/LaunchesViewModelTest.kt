package com.example.launches.viewmodel.list

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
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class LaunchesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val getLaunchesUseCase: GetLaunchesUseCase = mockk()
    private lateinit var viewModel: LaunchesViewModel

    // HELPER: Fondamentale per far funzionare il Mapper (toUiModel) senza crashare
    private fun createDummyLaunch(id: String): Launch {
        return Launch(
            id = id,
            missionName = "Mission $id",
            launchDate = ZonedDateTime.now(),
            isSuccess = true,
            rocketId = "rocket_$id",
            rocketName = "Falcon 9",
            patchImageUrl = null,
            webcastUrl = null,
            articleUrl = null,
            wikipediaUrl = null,
            details = null,
            flickrImages = emptyList() // Importante: il mapper chiama .toImmutableList() su questo
        )
    }

    @Test
    fun `GIVEN UseCase returns success WHEN init THEN state is Success with mapped data`() =
        runTest(testDispatcher) {
            // GIVEN
            val launchId = "1"
            val domainData = listOf(createDummyLaunch(launchId))

            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(domainData)
            )

            // WHEN
            viewModel = LaunchesViewModel(getLaunchesUseCase)

            // THEN
            viewModel.uiState.test {
                assertThat(awaitItem()).isEqualTo(LaunchesUiState.Loading)
                advanceUntilIdle()

                val successState = awaitItem()
                assertThat(successState).isInstanceOf(LaunchesUiState.Success::class.java)

                // FIX: Verifichiamo le proprietà del UiModel, non l'oggetto Launch intero
                val uiList = (successState as LaunchesUiState.Success).launches
                assertThat(uiList).hasSize(1)
                assertThat(uiList[0].id).isEqualTo(launchId)
                assertThat(uiList[0].missionName).isEqualTo("Mission 1")
            }
        }

    @Test
    fun `GIVEN UseCase returns failure WHEN init THEN state is Error`() = runTest(testDispatcher) {
        // GIVEN
        coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
            DomainResult.Failure(DataError.Network.RequestTimeout)
        )

        // WHEN
        viewModel = LaunchesViewModel(getLaunchesUseCase)

        // THEN
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(LaunchesUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LaunchesUiState.Error::class.java)
            assertThat((errorState as LaunchesUiState.Error).error)
                .isEqualTo(DataError.Network.RequestTimeout)
        }
    }

    @Test
    fun `GIVEN existing data WHEN Refresh Intent succeed THEN show new data`() =
        runTest(testDispatcher) {
            // GIVEN - Initial load
            val oldLaunch = createDummyLaunch("old")
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(listOf(oldLaunch))
            )

            viewModel = LaunchesViewModel(getLaunchesUseCase)
            advanceUntilIdle()

            // PREPARE REFRESH
            val newLaunch = createDummyLaunch("new")
            coEvery { getLaunchesUseCase(forceRefresh = true) } returns flowOf(
                DomainResult.Success(listOf(newLaunch))
            )

            viewModel.uiState.test {
                val initialState = awaitItem() // Consume initial state
                assertThat(initialState).isInstanceOf(LaunchesUiState.Success::class.java)

                // WHEN - MVI Style
                viewModel.process(LaunchesIntent.Refresh)
                advanceUntilIdle()

                // THEN
                val result = awaitItem()
                assertThat(result).isInstanceOf(LaunchesUiState.Success::class.java)

                // FIX: Verifichiamo che l'ID sia cambiato nel UiModel
                val uiList = (result as LaunchesUiState.Success).launches
                assertThat(uiList).hasSize(1)
                assertThat(uiList[0].id).isEqualTo("new")
            }
        }

    @Test
    fun `GIVEN existing data WHEN Refresh Intent fails THEN preserves old data`() =
        runTest(testDispatcher) {
            // GIVEN - Initial load
            val oldLaunch = createDummyLaunch("old")
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(listOf(oldLaunch))
            )

            viewModel = LaunchesViewModel(getLaunchesUseCase)
            advanceUntilIdle()

            // PREPARE REFRESH FAIL
            coEvery { getLaunchesUseCase(forceRefresh = true) } returns flowOf(
                DomainResult.Failure(DataError.Network.Server)
            )

            viewModel.uiState.test {
                awaitItem() // Consume initial state

                // WHEN
                viewModel.process(LaunchesIntent.Refresh)
                advanceUntilIdle()

                // THEN
                val errorState = awaitItem()
                assertThat(errorState).isInstanceOf(LaunchesUiState.Error::class.java)
                val state = errorState as LaunchesUiState.Error

                assertThat(state.error).isEqualTo(DataError.Network.Server)

                // FIX: Verifichiamo che i dati vecchi siano preservati (e mappati)
                assertThat(state.launches).hasSize(1)
                assertThat(state.launches[0].id).isEqualTo("old")
            }
        }

    @Test
    fun `GIVEN LaunchClicked Intent WHEN process THEN emits NavigateToDetail Effect`() =
        runTest(testDispatcher) {
            // GIVEN
            val launchId = "123"
            coEvery { getLaunchesUseCase(forceRefresh = false) } returns flowOf(
                DomainResult.Success(emptyList())
            )

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
