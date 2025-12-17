package com.example.data.mappers

import com.example.data.GetPastLaunchesQuery
import com.google.common.truth.Truth.assertThat
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
        assertThat(result.id).isEqualTo(launch.id)
        assertThat(result.missionName).isEqualTo(launch.mission_name)
        assertThat(result.launchDate).isEqualTo(ZonedDateTime.parse(inputDateString))
        assertThat(result.isSuccess).isEqualTo(launch.launch_success)
        assertThat(result.details).isEqualTo(launch.details)
        assertThat(result.rocketId).isEqualTo(launch.rocket?.rocket?.id)
        assertThat(result.rocketName).isEqualTo(launch.rocket?.rocket_name)
        assertThat(result.patchImageUrl).isEqualTo(launch.links?.mission_patch_small)
        assertThat(result.flickrImages).isEqualTo(launch.links?.flickr_images)
        assertThat(result.webcastUrl).isEqualTo(launch.links?.video_link)
        assertThat(result.articleUrl).isEqualTo(launch.links?.article_link)
        assertThat(result.wikipediaUrl).isEqualTo(launch.links?.wikipedia)
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
        assertThat(result.id).isEqualTo(launch.id)
        assertThat(result.missionName).isNull()
        assertThat(result.launchDate).isEqualTo(ZonedDateTime.parse(inputDateString))
        assertThat(result.isSuccess).isFalse()
        assertThat(result.details).isNull()
        assertThat(result.rocketId).isNull()
        assertThat(result.rocketName).isNull()
        assertThat(result.patchImageUrl).isNull()
        assertThat(result.flickrImages).isEmpty()
        assertThat(result.webcastUrl).isNull()
        assertThat(result.articleUrl).isNull()
        assertThat(result.wikipediaUrl).isNull()
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
        assertThat(result.launchDate.dayOfMonth).isEqualTo(now.dayOfMonth)
        assertThat(result.launchDate.month).isEqualTo(now.month)
        assertThat(result.launchDate.year).isEqualTo(now.year)
    }
}
