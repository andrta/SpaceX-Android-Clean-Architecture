
package com.example.data.mappers

import com.example.data.GetPastLaunchesQuery
import com.google.common.truth.Truth
import org.junit.Test
import java.time.ZonedDateTime

class ApolloLaunchMapperTest {
    @Test
    fun `GIVEN a GetLaunchesQuery Launch object WHEN toDomain is called THEN it should be correctly mapped to a Launch domain object`() {
        // GIVEN
        val inputDateString = "2022-02-01T10:00:00.000Z"
        val launch = GetPastLaunchesQuery.LaunchesPast(
            id = "id",
            mission_name = "mission_name",
            launch_date_utc = inputDateString,
            launch_success = true,
            details = "details",
            rocket = GetPastLaunchesQuery.Rocket(
                rocket_name = "rocket_name",
                rocket = GetPastLaunchesQuery.Rocket1("rocket_id"),
            ),
            links = GetPastLaunchesQuery.Links(
                mission_patch_small = "mission_patch_small",
                flickr_images = listOf("flickr_images"),
                video_link = "video_link",
                article_link = "article_link",
                wikipedia = "wikipedia"
            )
        )

        // WHEN
        val result = launch.toDomain()

        // THEN
        Truth.assertThat(result.id).isEqualTo(launch.id)
        Truth.assertThat(result.missionName).isEqualTo(launch.mission_name)
        Truth.assertThat(result.launchDate).isEqualTo(ZonedDateTime.parse(inputDateString))
        Truth.assertThat(result.isSuccess).isEqualTo(launch.launch_success)
        Truth.assertThat(result.details).isEqualTo(launch.details)
        Truth.assertThat(result.rocketId).isEqualTo(launch.rocket?.rocket?.id)
        Truth.assertThat(result.rocketName).isEqualTo(launch.rocket?.rocket_name)
        Truth.assertThat(result.patchImageUrl).isEqualTo(launch.links?.mission_patch_small)
        Truth.assertThat(result.flickrImages).isEqualTo(launch.links?.flickr_images)
        Truth.assertThat(result.webcastUrl).isEqualTo(launch.links?.video_link)
        Truth.assertThat(result.articleUrl).isEqualTo(launch.links?.article_link)
        Truth.assertThat(result.wikipediaUrl).isEqualTo(launch.links?.wikipedia)
    }

    @Test
    fun `GIVEN a GetLaunchesQuery Launch object with nullable values WHEN toDomain is called THEN it should be correctly mapped to a Launch domain object`() {
        // GIVEN
        val inputDateString = "2022-02-01T10:00:00.000Z"
        val launch = GetPastLaunchesQuery.LaunchesPast(
            id = "id",
            mission_name = null,
            launch_date_utc = inputDateString,
            launch_success = null,
            details = null,
            rocket = null,
            links = null
        )

        // WHEN
        val result = launch.toDomain()

        // THEN
        Truth.assertThat(result.id).isEqualTo(launch.id)
        Truth.assertThat(result.missionName).isNull()
        Truth.assertThat(result.launchDate).isEqualTo(ZonedDateTime.parse(inputDateString))
        Truth.assertThat(result.isSuccess).isFalse()
        Truth.assertThat(result.details).isNull()
        Truth.assertThat(result.rocketId).isNull()
        Truth.assertThat(result.rocketName).isNull()
        Truth.assertThat(result.patchImageUrl).isNull()
        Truth.assertThat(result.flickrImages).isEmpty()
        Truth.assertThat(result.webcastUrl).isNull()
        Truth.assertThat(result.articleUrl).isNull()
        Truth.assertThat(result.wikipediaUrl).isNull()
    }

    @Test
    fun `GIVEN a GetLaunchesQuery Launch object with an invalid date WHEN toDomain is called THEN the date should be now`() {
        // GIVEN
        val launch = GetPastLaunchesQuery.LaunchesPast(
            id = "id",
            mission_name = "mission_name",
            launch_date_utc = "invalid date",
            launch_success = null,
            details = null,
            rocket = null,
            links = null
        )
        val now = ZonedDateTime.now()

        // WHEN
        val result = launch.toDomain()

        // THEN
        Truth.assertThat(result.launchDate.dayOfMonth).isEqualTo(now.dayOfMonth)
        Truth.assertThat(result.launchDate.month).isEqualTo(now.month)
        Truth.assertThat(result.launchDate.year).isEqualTo(now.year)
    }
}
