package com.example.data.mappers

import com.example.data.GetPastLaunchesQuery
import com.example.domain.exception.DataException
import com.example.domain.models.Launch
import java.time.ZonedDateTime

fun GetPastLaunchesQuery.LaunchesPast.toDomain(): Launch {
    return Launch(
        id = this.id ?: throw DataException.NullValueException(),
        missionName = this.mission_name,

        launchDate = try {
            if (this.launch_date_utc != null) {
                ZonedDateTime.parse(this.launch_date_utc.toString())
            } else {
                ZonedDateTime.now()
            }
        } catch (e: Exception) {
            ZonedDateTime.now()
        },

        isSuccess = this.launch_success,
        details = this.details,

        rocketName = this.rocket?.rocket_name,
        rocketId = this.rocket?.rocket?.id,

        patchImageUrl = this.links?.flickr_images?.firstOrNull(),
        webcastUrl = this.links?.video_link,
        articleUrl = this.links?.article_link,
        wikipediaUrl = this.links?.wikipedia,

        flickrImages = this.links?.flickr_images?.filterNotNull() ?: emptyList()
    )
}
