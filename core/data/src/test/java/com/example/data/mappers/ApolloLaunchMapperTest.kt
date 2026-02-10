package com.example.data.mappers

import com.example.data.GetPastLaunchesQuery
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZonedDateTime

class ApolloLaunchMapperTest {

    @Test
    fun `GIVEN a valid Launch Query WHEN toDomain is called THEN maps all fields correctly`() {
        // GIVEN
        val inputDateString = "2022-02-01T10:00:00.000Z"

        val links = GetPastLaunchesQuery.Links(
            mission_patch_small = "https://patch.url/small.png",
            mission_patch = "https://patch.url/large.png",
            flickr_images = listOf("https://flickr.com/img1.jpg", "https://flickr.com/img2.jpg"),
            video_link = "https://youtube.com/video",
            article_link = "https://article.link",
            wikipedia = "https://wiki.link"
        )

        val rocketNode = GetPastLaunchesQuery.Rocket1(id = "falcon9_id")
        val rocket = GetPastLaunchesQuery.Rocket(
            rocket_name = "Falcon 9",
            rocket = rocketNode
        )

        val queryLaunch = GetPastLaunchesQuery.LaunchesPast(
            id = "101",
            mission_name = "Starlink 1",
            launch_date_utc = inputDateString,
            launch_success = true,
            details = "Successful launch",
            rocket = rocket,
            links = links
        )

        // WHEN
        val domainLaunch = queryLaunch.toDomain()

        // THEN
        assertThat(domainLaunch.id).isEqualTo("101")
        assertThat(domainLaunch.missionName).isEqualTo("Starlink 1")
        assertThat(domainLaunch.launchDate).isEqualTo(ZonedDateTime.parse(inputDateString))
        assertThat(domainLaunch.isSuccess).isTrue()
        assertThat(domainLaunch.details).isEqualTo("Successful launch")

        assertThat(domainLaunch.rocketName).isEqualTo("Falcon 9")
        assertThat(domainLaunch.rocketId).isEqualTo("falcon9_id")

        assertThat(domainLaunch.patchImageUrl).isEqualTo("https://patch.url/small.png")

        assertThat(domainLaunch.flickrImages).hasSize(2)
        assertThat(domainLaunch.flickrImages).containsExactly("https://flickr.com/img1.jpg", "https://flickr.com/img2.jpg")

        assertThat(domainLaunch.webcastUrl).isEqualTo("https://youtube.com/video")
        assertThat(domainLaunch.articleUrl).isEqualTo("https://article.link")
        assertThat(domainLaunch.wikipediaUrl).isEqualTo("https://wiki.link")
    }

    @Test
    fun `GIVEN null fields WHEN toDomain is called THEN uses default values`() {
        // GIVEN
        val inputDateString = "2022-02-01T10:00:00.000Z"

        val queryLaunch = GetPastLaunchesQuery.LaunchesPast(
            id = "102",
            mission_name = null,
            launch_date_utc = inputDateString,
            launch_success = null,
            details = null,
            rocket = null,
            links = null
        )

        // WHEN
        val domainLaunch = queryLaunch.toDomain()

        // THEN
        assertThat(domainLaunch.id).isEqualTo("102")
        assertThat(domainLaunch.missionName).isEqualTo("Unknown Mission")
        assertThat(domainLaunch.isSuccess).isFalse()
        assertThat(domainLaunch.rocketName).isEqualTo("Unknown Rocket")
        assertThat(domainLaunch.rocketId).isNull()
        assertThat(domainLaunch.patchImageUrl).isNull()
        assertThat(domainLaunch.flickrImages).isEmpty()
    }

    @Test
    fun `GIVEN invalid date WHEN toDomain is called THEN returns current time`() {
        // GIVEN
        val queryLaunch = GetPastLaunchesQuery.LaunchesPast(
            id = "103",
            mission_name = "Test",
            launch_date_utc = "invalid-date-format",
            launch_success = false,
            details = null,
            rocket = null,
            links = null
        )

        val beforeTest = ZonedDateTime.now().minusSeconds(1)

        // WHEN
        val domainLaunch = queryLaunch.toDomain()

        val afterTest = ZonedDateTime.now().plusSeconds(1)

        // THEN
        assertThat(domainLaunch.launchDate).isAtLeast(beforeTest)
        assertThat(domainLaunch.launchDate).isAtMost(afterTest)
    }
}
