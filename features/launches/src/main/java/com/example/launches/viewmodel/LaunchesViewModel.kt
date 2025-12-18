package com.example.launches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Launch
import com.example.domain.result.DomainResult
import com.example.launches.model.LaunchesUiState
import com.example.launches.usecases.GetLaunchesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchesViewModel @Inject constructor(
    private val getLaunchesUseCase: GetLaunchesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LaunchesUiState>(LaunchesUiState.Loading)
    val uiState: StateFlow<LaunchesUiState> = _uiState

    init {
        loadLaunches(forceRefresh = false)
    }

    fun onRefresh() {
        loadLaunches(forceRefresh = true)
    }

    private fun loadLaunches(forceRefresh: Boolean) {
        viewModelScope.launch {
            if (_uiState.value !is LaunchesUiState.Success) {
                _uiState.value = LaunchesUiState.Loading
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
                        launches = result.data,
                        isRefreshing = false
                    )
                }

                is DomainResult.Failure -> {
                    val oldData = (currentState as? LaunchesUiState.Success)?.launches
                        ?: (currentState as? LaunchesUiState.Error)?.launches
                        ?: emptyList()

                    LaunchesUiState.Error(
                        error = result.error,
                        launches = oldData
                    )
                }
            }
        }
    }
}
