package com.example.data.mappers

import com.example.data.models.dto.FlickrDto
import com.example.data.models.dto.LaunchDto
import com.example.data.models.dto.LinksDto
import com.example.data.models.dto.PatchDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZonedDateTime

class RestLaunchMapperTest {

    @Test
    fun `GIVEN a LaunchDto object WHEN toDomain is called THEN it should be correctly mapped to a Launch domain object`() {
        // GIVEN
        val launchDto = LaunchDto(
            id = "id",
            name = "name",
            dateUtc = "2022-02-01T10:00:00.000Z",
            success = true,
            details = "details",
            rocketId = "rocketId",
            links = LinksDto(
                patch = PatchDto(
                    small = "small",
                    large = "large",
                ),
                flickr = FlickrDto(
                    original = listOf("original"),
                    small = listOf("small"),
                ),
                webcast = "webcast",
                article = "article",
                wikipedia = "wikipedia",
            )
        )

        // WHEN
        val result = launchDto.toDomain(rocketName = "rocketName")

        // THEN
        assertThat(result.id).isEqualTo(launchDto.id)
        assertThat(result.missionName).isEqualTo(launchDto.name)
        assertThat(result.launchDate).isEqualTo(ZonedDateTime.parse(launchDto.dateUtc))
        assertThat(result.isSuccess).isEqualTo(launchDto.success)
        assertThat(result.details).isEqualTo(launchDto.details)
        assertThat(result.rocketId).isEqualTo(launchDto.rocketId)
        assertThat(result.rocketName).isEqualTo("rocketName")
        assertThat(result.patchImageUrl).isEqualTo(launchDto.links?.patch?.small)
        assertThat(result.flickrImages).isEqualTo(launchDto.links?.flickr?.original)
        assertThat(result.webcastUrl).isEqualTo(launchDto.links?.webcast)
        assertThat(result.articleUrl).isEqualTo(launchDto.links?.article)
        assertThat(result.wikipediaUrl).isEqualTo(launchDto.links?.wikipedia)
    }

    @Test
    fun `GIVEN a LaunchDto object with nullable values WHEN toDomain is called THEN it should be correctly mapped to a Launch domain object`() {
        // GIVEN
        val launchDto = LaunchDto(
            id = "id",
            name = "name",
            dateUtc = "2022-02-01T10:00:00.000Z",
            success = null,
            details = null,
            rocketId = "rocketId",
            links = null
        )

        // WHEN
        val result = launchDto.toDomain(rocketName = "rocketName")

        // THEN
        assertThat(result.id).isEqualTo(launchDto.id)
        assertThat(result.missionName).isEqualTo(launchDto.name)
        assertThat(result.launchDate).isEqualTo(ZonedDateTime.parse(launchDto.dateUtc))
        assertThat(result.isSuccess).isFalse()
        assertThat(result.details).isNull()
        assertThat(result.rocketId).isEqualTo(launchDto.rocketId)
        assertThat(result.rocketName).isEqualTo("rocketName")
        assertThat(result.patchImageUrl).isNull()
        assertThat(result.flickrImages).isEmpty()
        assertThat(result.webcastUrl).isNull()
        assertThat(result.articleUrl).isNull()
        assertThat(result.wikipediaUrl).isNull()
    }

    @Test
    fun `GIVEN a LaunchDto object with an invalid date WHEN toDomain is called THEN the date should be now`() {
        // GIVEN
        val launchDto = LaunchDto(
            id = "id",
            name = "name",
            dateUtc = "invalid date",
            success = null,
            details = null,
            rocketId = "rocketId",
            links = null
        )
        val now = ZonedDateTime.now()

        // WHEN
        val result = launchDto.toDomain(rocketName = "rocketName")

        // THEN
        assertThat(result.launchDate.dayOfMonth).isEqualTo(now.dayOfMonth)
        assertThat(result.launchDate.month).isEqualTo(now.month)
        assertThat(result.launchDate.year).isEqualTo(now.year)
    }
}
