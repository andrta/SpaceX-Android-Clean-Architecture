package com.example.localstorage.mappers

import com.example.domain.models.Launch
import com.example.localstorage.entities.LaunchEntity
import com.example.domain.exception.DataException
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZonedDateTime

class LaunchEntityMapperTest {
    @Test
    fun `GIVEN LaunchEntity WHEN toDomain called THEN maps correctly`() {
        // GIVEN
        val entity = LaunchEntity(
            id = "123",
            missionName = "Mission X",
            launchDate = ZonedDateTime.parse("2022-01-01T10:00:00Z"),
            isSuccess = true,
            rocketId = "r1",
            rocketName = "Falcon 9",
            details = "Details",
            patchImageUrl = "url",
            webcastUrl = null,
            articleUrl = null,
            wikipediaUrl = null,
            flickrImages = listOf("img1"),
            userNotes = "Note"
        )

        // WHEN
        val domain = entity.toDomain()

        // THEN
        assertThat(domain.id).isEqualTo("123")
        assertThat(domain.missionName).isEqualTo("Mission X")
        assertThat(domain.rocketName).isEqualTo("Falcon 9")
        assertThat(domain.flickrImages).contains("img1")
    }

    @Test
    fun `GIVEN valid Launch WHEN toEntity called THEN maps correctly`() {
        // GIVEN
        val launch = Launch(
            id = "123",
            missionName = "Mission X",
            launchDate = ZonedDateTime.now(),
            isSuccess = true,
            rocketId = "r1",
            rocketName = "Falcon 9",
            details = null,
            patchImageUrl = null,
            webcastUrl = null,
            articleUrl = null,
            wikipediaUrl = null,
            flickrImages = emptyList()
        )

        // WHEN
        val entity = launch.toEntity()

        // THEN
        assertThat(entity.id).isEqualTo("123")
        assertThat(entity.missionName).isEqualTo("Mission X")
        assertThat(entity.userNotes).isNull() // Default value check
    }

    @Test(expected = DataException.NullValueException::class)
    fun `GIVEN Launch with null MissionName WHEN toEntity called THEN throws DataException`() {
        // GIVEN
        val launch = Launch(
            id = "1",
            missionName = null, // Invalid
            launchDate = ZonedDateTime.now(),
            isSuccess = true,
            rocketId = "r1",
            rocketName = "Falcon",
            details = null, patchImageUrl = null, webcastUrl = null, articleUrl = null, wikipediaUrl = null, flickrImages = emptyList()
        )

        // WHEN
        launch.toEntity()
    }
}
