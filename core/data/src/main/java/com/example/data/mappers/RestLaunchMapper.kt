package com.example.data.mappers


import com.example.data.models.dto.LaunchDto
import com.example.domain.exception.DataException
import com.example.domain.models.Launch
import java.time.ZonedDateTime

fun LaunchDto.toDomain(rocketName: String): Launch {
    return Launch(
        id = this.id ?: throw DataException.NullValueException(),
        missionName = this.name,

        launchDate = try {
            ZonedDateTime.parse(this.dateUtc)
        } catch (e: Exception) {
            ZonedDateTime.now()
        },
        isSuccess = this.success ?: false,

        rocketId = this.rocketId,
        rocketName = rocketName,

        details = this.details,
        patchImageUrl = this.links?.flickr?.original?.firstOrNull(),
        webcastUrl = this.links?.webcast,
        articleUrl = this.links?.article,
        wikipediaUrl = this.links?.wikipedia,
        flickrImages = this.links?.flickr?.original ?: emptyList()
    )
}
