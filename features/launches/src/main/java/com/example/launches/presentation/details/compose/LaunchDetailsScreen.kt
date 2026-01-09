package com.example.launches.presentation.details.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.launches.R
import com.example.launches.model.LaunchDetailsIntent
import com.example.launches.model.LaunchDetailsUiEffect
import com.example.launches.model.LaunchDetailsUiState
import com.example.launches.model.LaunchUiModel
import com.example.launches.viewmodel.details.LaunchDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchDetailsScreen(
    viewModel: LaunchDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is LaunchDetailsUiEffect.NavigateBack -> onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mission Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val s = state) {
                is LaunchDetailsUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is LaunchDetailsUiState.Error -> {
                    ErrorContent(
                        message = s.message,
                        onRetry = { viewModel.process(LaunchDetailsIntent.Retry) }
                    )
                }

                is LaunchDetailsUiState.Success -> {
                    SuccessContent(launchUiModel = s.launch)
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(launchUiModel: LaunchUiModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(launchUiModel.patchImageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            contentDescription = "Mission Patch",
            modifier = Modifier
                .size(120.dp)
                .padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            LabeledInfo(
                label = "Mission Name",
                value = launchUiModel.missionName,
                isTitle = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledInfo(label = "Rocket", value = launchUiModel.rocketName)

            Spacer(modifier = Modifier.height(16.dp))

            LabeledInfo(label = "Launch Date", value = launchUiModel.launchDate.toString())

            Spacer(modifier = Modifier.height(24.dp))

            launchUiModel.isSuccess?.let { LaunchStatus(isSuccess = it) }
        }
    }
}

@Composable
private fun LabeledInfo(label: String, value: String, isTitle: Boolean = false) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = if (isTitle) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
            fontWeight = if (isTitle) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun LaunchStatus(isSuccess: Boolean) {
    val iconRes = if (isSuccess) R.drawable.ic_check_circle_24 else R.drawable.ic_warning_24
    val color = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336) // Green or Red
    val text = if (isSuccess) "Successful Launch" else "Launch Failed"

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
