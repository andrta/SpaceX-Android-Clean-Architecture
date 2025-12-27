package com.example.localstorage.mappers

import com.example.domain.models.Launch
import com.example.localstorage.entities.LaunchEntity
import com.example.domain.exception.DataException

fun LaunchEntity.toDomain(): Launch {
    return Launch(
        id = this.id,
        missionName = this.missionName,
        launchDate = this.launchDate,
        isSuccess = this.isSuccess,
        rocketId = this.rocketId,
        rocketName = this.rocketName,
        details = this.details,
        patchImageUrl = this.patchImageUrl,
        webcastUrl = this.webcastUrl,
        articleUrl = this.articleUrl,
        wikipediaUrl = this.wikipediaUrl,
        flickrImages = this.flickrImages,
    )
}

fun Launch.toEntity(): LaunchEntity {
    return LaunchEntity(
        id = this.id,
        missionName = this.missionName ?: throw DataException.NullValueException(),
        launchDate = this.launchDate,
        isSuccess = this.isSuccess,
        rocketId = this.rocketId ?: throw DataException.NullValueException(),
        rocketName = this.rocketName ?: throw DataException.NullValueException(),
        details = this.details,
        patchImageUrl = this.patchImageUrl,
        webcastUrl = this.webcastUrl,
        articleUrl = this.articleUrl,
        wikipediaUrl = this.wikipediaUrl,
        flickrImages = this.flickrImages,
        userNotes = null,
    )
}
