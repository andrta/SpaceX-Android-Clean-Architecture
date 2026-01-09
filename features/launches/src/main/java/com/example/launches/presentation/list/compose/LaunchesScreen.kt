package com.example.launches.presentation.list.compose

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.launches.model.LaunchUiModel
import com.example.launches.model.LaunchesIntent
import com.example.launches.model.LaunchesUiEffect
import com.example.launches.model.LaunchesUiState
import com.example.launches.presentation.list.compose.components.LaunchItem
import com.example.launches.viewmodel.list.LaunchesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchesScreen(
    viewModel: LaunchesViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is LaunchesUiEffect.NavigateToDetail -> onNavigateToDetail(effect.launchId)
                    is LaunchesUiEffect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SpaceX Launches",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LaunchesContent(
                uiState = uiState,
                onIntent = viewModel::process
            )
        }
    }
}

@Composable
fun LaunchesContent(
    uiState: LaunchesUiState,
    onIntent: (LaunchesIntent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        when (uiState) {
            is LaunchesUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is LaunchesUiState.Error -> {
                if (uiState.launches.isNotEmpty()) {
                    LaunchesList(
                        launches = uiState.launches,
                        onIntent = onIntent
                    )
                } else {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Error: ${uiState.error}")
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = { onIntent(LaunchesIntent.Refresh) }) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            is LaunchesUiState.Success -> {
                LaunchesList(
                    launches = uiState.launches,
                    onIntent = onIntent
                )
            }
        }
    }
}

@Composable
fun LaunchesList(
    launches: List<LaunchUiModel>,
    onIntent: (LaunchesIntent) -> Unit
) {
    LazyColumn {
        items(
            items = launches,
            key = { it.id }
        ) { launchUiModel ->
            LaunchItem(
                launchUiModel = launchUiModel,
                onLaunchClick = { onIntent(LaunchesIntent.LaunchClicked(launchUiModel.id)) }
            )
        }
    }
}
