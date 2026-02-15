package com.example.launches.viewmodel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Launch
import com.example.domain.result.DomainResult
import com.example.launches.mappers.toUiModel
import com.example.launches.model.LaunchesIntent
import com.example.launches.model.LaunchesUiEffect
import com.example.launches.model.LaunchesUiState
import com.example.launches.usecases.GetLaunchesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchesViewModel @Inject constructor(
    private val getLaunchesUseCase: GetLaunchesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<LaunchesUiState>(LaunchesUiState.Loading)
    val uiState: StateFlow<LaunchesUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<LaunchesUiEffect>(BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        loadLaunches(forceRefresh = false)
    }

    fun process(intent: LaunchesIntent) {
        when (intent) {
            is LaunchesIntent.Refresh -> {
                loadLaunches(forceRefresh = true)
            }

            is LaunchesIntent.LaunchClicked -> {
                sendEffect(LaunchesUiEffect.NavigateToDetail(intent.id))
            }
        }
    }

    private fun loadLaunches(forceRefresh: Boolean) {
        viewModelScope.launch {
            if (_uiState.value !is LaunchesUiState.Success) {
                _uiState.update { LaunchesUiState.Loading }
            }

            getLaunchesUseCase(forceRefresh).collect { result ->
                processResult(result)
            }
        }
    }

    private fun processResult(result: DomainResult<List<Launch>>) {
        _uiState.update { currentState ->
            when (result) {
                is DomainResult.Success -> {
                    LaunchesUiState.Success(
                        launches = result.data
                            .map { it.toUiModel() }
                            .toImmutableList(),
                    )
                }

                is DomainResult.Failure -> {
                    val oldData = (currentState as? LaunchesUiState.Success)?.launches
                        ?: (currentState as? LaunchesUiState.Error)?.launches
                        ?: persistentListOf()

                    sendEffect(LaunchesUiEffect.ShowToast(result.error.toString()))

                    LaunchesUiState.Error(
                        error = result.error,
                        launches = oldData
                    )
                }
            }
        }
    }

    private fun sendEffect(effect: LaunchesUiEffect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }
}
