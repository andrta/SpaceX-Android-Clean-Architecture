package com.example.launches.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.models.Launch
import com.example.launches.model.LaunchesUiState
import com.example.launches.presentation.compose.components.LaunchItem
import com.example.launches.viewmodel.LaunchesViewModel

@Composable
fun LaunchesScreen(
    viewModel: LaunchesViewModel = hiltViewModel(),
    onLaunchClick: () -> Unit
) {
    // Collect Flow in a lifecycle-aware manner
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        when (val state = uiState) {
            is LaunchesUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is LaunchesUiState.Error -> {
                if (state.launches.isNotEmpty()) {
                    LaunchesList(state.launches, onLaunchClick)
                } else {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Error: ${state.error}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.onRefresh() }) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            is LaunchesUiState.Success -> {
                LaunchesList(state.launches, onLaunchClick)
            }
        }
    }
}

@Composable
fun LaunchesList(launches: List<Launch>, onLaunchClick: () -> Unit) {
    LazyColumn {
        items(launches) { launch ->
            LaunchItem(launch = launch, onLaunchClick = onLaunchClick)
        }
    }
}
