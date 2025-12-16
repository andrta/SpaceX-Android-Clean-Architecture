package com.example.domain.models

import java.time.ZonedDateTime

data class Launch(
    val id: String,
    val missionName: String,
    val launchDate: ZonedDateTime,
    val isSuccess: Boolean,

    val rocketId: String,
    val rocketName: String,

    val patchImageUrl: String?,
    val webcastUrl: String?,
    val articleUrl: String?,

    val details: String?,
    val flickrImages: List<String>
)
