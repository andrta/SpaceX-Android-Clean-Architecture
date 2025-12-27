package com.example.launches.mappers

import com.example.domain.models.Launch
import com.example.launches.model.LaunchUiModel
import kotlinx.collections.immutable.toImmutableList

fun Launch.toUiModel(): LaunchUiModel {
    return LaunchUiModel(
        id = id,
        missionName = missionName ?: "Unknown Mission",
        launchDate = launchDate,
        isSuccess = isSuccess,
        rocketName = rocketName ?: "Unknown Rocket",
        patchImageUrl = patchImageUrl,
        details = details,
        flickrImages = flickrImages.toImmutableList()
    )
}
