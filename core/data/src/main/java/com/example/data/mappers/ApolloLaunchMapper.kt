package com.example.data.mappers

import com.example.data.GetPastLaunchesQuery
import com.example.domain.exception.DataException
import com.example.domain.models.Launch
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

fun GetPastLaunchesQuery.LaunchesPast.toDomain(): Launch {
    return Launch(
        id = this.id ?: throw DataException.NullValueException(),
        missionName = this.mission_name ?: "Unknown Mission",
        launchDate = parseDateSafely(this.launch_date_utc),
        isSuccess = this.launch_success ?: false,
        details = this.details,
        rocketName = this.rocket?.rocket_name ?: "Unknown Rocket",
        rocketId = this.rocket?.rocket?.id,
        patchImageUrl = this.links?.mission_patch_small ?: this.links?.mission_patch,
        webcastUrl = this.links?.video_link,
        articleUrl = this.links?.article_link,
        wikipediaUrl = this.links?.wikipedia,
        flickrImages = this.links?.flickr_images?.filterNotNull() ?: emptyList()
    )
}

private fun parseDateSafely(dateString: Any?): ZonedDateTime {
    return try {
        if (dateString != null) {
            ZonedDateTime.parse(dateString.toString())
        } else {
            ZonedDateTime.now()
        }
    } catch (e: DateTimeParseException) {
        ZonedDateTime.now()
    } catch (e: Exception) {
        ZonedDateTime.now()
    }
}
