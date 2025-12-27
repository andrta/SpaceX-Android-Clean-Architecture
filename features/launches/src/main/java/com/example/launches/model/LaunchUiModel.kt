package com.example.launches.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import java.time.ZonedDateTime

@Immutable
data class LaunchUiModel(
    val id: String,
    val missionName: String,
    val launchDate: ZonedDateTime,
    val isSuccess: Boolean,
    val rocketName: String,
    val patchImageUrl: String?,
    val details: String?,
    val flickrImages: ImmutableList<String>
)
