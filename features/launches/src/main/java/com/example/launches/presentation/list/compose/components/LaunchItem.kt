package com.example.launches.presentation.list.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.launches.R
import com.example.launches.model.LaunchUiModel
import java.time.format.DateTimeFormatter

@Composable
fun LaunchItem(launchUiModel: LaunchUiModel, onLaunchClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onLaunchClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(launchUiModel.patchImageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = launchUiModel.missionName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = launchUiModel.rocketName,
                    style = MaterialTheme.typography.bodyMedium
                )

                val dateStr =
                    launchUiModel.launchDate.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "N/A"
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            val statusColor =
                if (launchUiModel.isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            Icon(
                imageVector = if (launchUiModel.isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = "Status",
                tint = statusColor
            )
        }
    }
}
