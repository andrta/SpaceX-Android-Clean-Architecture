package com.example.launches.viewmodel.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.result.DomainResult
import com.example.launches.mappers.toUiModel
import com.example.launches.model.LaunchDetailsIntent
import com.example.launches.model.LaunchDetailsUiEffect
import com.example.launches.model.LaunchDetailsUiState
import com.example.launches.usecases.GetLaunchDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchDetailsViewModel @Inject constructor(
    private val getLaunchDetailsUseCase: GetLaunchDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val launchId: String = checkNotNull(savedStateHandle["launchId"]) {
        "Launch ID not found in arguments"
    }

    private val _uiState = MutableStateFlow<LaunchDetailsUiState>(LaunchDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<LaunchDetailsUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        loadData()
    }

    fun process(intent: LaunchDetailsIntent) {
        when (intent) {
            is LaunchDetailsIntent.Retry -> loadData()
            is LaunchDetailsIntent.BackClicked -> {
                viewModelScope.launch { _uiEffect.send(LaunchDetailsUiEffect.NavigateBack) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { LaunchDetailsUiState.Loading }

            getLaunchDetailsUseCase(launchId).collect { result ->
                _uiState.update {
                    when (result) {
                        is DomainResult.Success -> LaunchDetailsUiState.Success(result.data.toUiModel())
                        is DomainResult.Failure -> LaunchDetailsUiState.Error(result.error.toString())
                    }
                }
            }
        }
    }
}
