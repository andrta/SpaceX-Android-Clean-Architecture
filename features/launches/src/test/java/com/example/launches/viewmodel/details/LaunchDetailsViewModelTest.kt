package com.example.launches.viewmodel.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.domain.models.Launch
import com.example.domain.result.DataError
import com.example.domain.result.DomainResult
import com.example.launches.model.LaunchDetailsIntent
import com.example.launches.model.LaunchDetailsUiEffect
import com.example.launches.model.LaunchDetailsUiState
import com.example.launches.usecases.GetLaunchDetailsUseCase
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
class LaunchDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val getLaunchDetailsUseCase: GetLaunchDetailsUseCase = mockk()

    private fun createSavedStateHandle(id: String) = SavedStateHandle(mapOf("launchId" to id))

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
            flickrImages = emptyList()
        )
    }

    private lateinit var viewModel: LaunchDetailsViewModel

    @Test
    fun `GIVEN valid ID WHEN init THEN loads data and shows Success`() = runTest(testDispatcher) {
        // GIVEN
        val launchId = "101"
        val mockLaunch = createDummyLaunch(launchId)
        coEvery { getLaunchDetailsUseCase(launchId) } returns flowOf(DomainResult.Success(mockLaunch))

        // WHEN
        viewModel = LaunchDetailsViewModel(
            getLaunchDetailsUseCase = getLaunchDetailsUseCase,
            savedStateHandle = createSavedStateHandle(launchId)
        )

        // THEN
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(LaunchDetailsUiState.Loading)

            advanceUntilIdle()

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(LaunchDetailsUiState.Success::class.java)

            val uiLaunch = (successState as LaunchDetailsUiState.Success).launch

            assertThat(uiLaunch.id).isEqualTo(mockLaunch.id)
            assertThat(uiLaunch.missionName).isEqualTo(mockLaunch.missionName)
        }
    }

    @Test
    fun `GIVEN error from usecase WHEN init THEN shows Error state`() = runTest(testDispatcher) {
        // GIVEN
        val launchId = "102"
        coEvery { getLaunchDetailsUseCase(launchId) } returns flowOf(DomainResult.Failure(DataError.Local.DiskRead))

        // WHEN
        viewModel = LaunchDetailsViewModel(
            getLaunchDetailsUseCase,
            createSavedStateHandle(launchId)
        )

        // THEN
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(LaunchDetailsUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(LaunchDetailsUiState.Error::class.java)
            assertThat((errorState as LaunchDetailsUiState.Error).message).contains("DiskRead")
        }
    }

    @Test
    fun `GIVEN Error state WHEN Retry Intent THEN reloads data`() = runTest(testDispatcher) {
        // GIVEN
        val launchId = "103"
        val mockLaunch = createDummyLaunch(launchId)

        coEvery { getLaunchDetailsUseCase(launchId) } returnsMany listOf(
            flowOf(DomainResult.Failure(DataError.Network.RequestTimeout)),
            flowOf(DomainResult.Success(mockLaunch))
        )

        viewModel = LaunchDetailsViewModel(getLaunchDetailsUseCase, createSavedStateHandle(launchId))
        advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState).isInstanceOf(LaunchDetailsUiState.Error::class.java)

            // WHEN
            viewModel.process(LaunchDetailsIntent.Retry)
            advanceUntilIdle()

            // THEN
            val loadingState = awaitItem()
            assertThat(loadingState).isEqualTo(LaunchDetailsUiState.Loading)

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(LaunchDetailsUiState.Success::class.java)
            assertThat(((successState as LaunchDetailsUiState.Success).launch).id).isEqualTo(launchId)
        }
    }

    @Test
    fun `GIVEN BackClicked Intent WHEN process THEN emits NavigateBack Effect`() =
        runTest(testDispatcher) {
            // GIVEN
            val launchId = "104"
            coEvery { getLaunchDetailsUseCase(launchId) } returns flowOf(DomainResult.Success(createDummyLaunch(launchId)))

            viewModel = LaunchDetailsViewModel(getLaunchDetailsUseCase, createSavedStateHandle(launchId))
            advanceUntilIdle()

            viewModel.uiEffect.test {
                // WHEN
                viewModel.process(LaunchDetailsIntent.BackClicked)

                // THEN
                assertThat(awaitItem()).isEqualTo(LaunchDetailsUiEffect.NavigateBack)
            }
        }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN missing ID in SavedStateHandle WHEN init THEN throws exception`() {
        viewModel = LaunchDetailsViewModel(
            getLaunchDetailsUseCase,
            SavedStateHandle()
        )
    }
}
