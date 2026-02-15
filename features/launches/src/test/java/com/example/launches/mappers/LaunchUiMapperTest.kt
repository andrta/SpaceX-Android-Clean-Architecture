package com.example.launches.mappers

import com.example.domain.models.Launch
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZonedDateTime

class LaunchUiMapperTest {
    private fun createLaunch(
        missionName: String? = "Test Mission",
        rocketName: String? = "Falcon 9",
        flickrImages: List<String> = listOf("img1", "img2")
    ): Launch {
        return Launch(
            id = "123",
            missionName = missionName,
            launchDate = ZonedDateTime.now(),
            isSuccess = true,
            rocketId = "rocket_1",
            rocketName = rocketName,
            patchImageUrl = "https://patch.url",
            webcastUrl = null,
            articleUrl = null,
            wikipediaUrl = null,
            details = "Some details",
            flickrImages = flickrImages
        )
    }

    @Test
    fun `toUiModel maps valid fields correctly`() {
        // GIVEN
        val launch = createLaunch(
            missionName = "Apollo 11",
            rocketName = "Saturn V",
            flickrImages = listOf("a", "b")
        )

        // WHEN
        val uiModel = launch.toUiModel()

        // THEN
        assertThat(uiModel.id).isEqualTo(launch.id)
        assertThat(uiModel.missionName).isEqualTo("Apollo 11")
        assertThat(uiModel.rocketName).isEqualTo("Saturn V")
        assertThat(uiModel.launchDate).isEqualTo(launch.launchDate)
        assertThat(uiModel.isSuccess).isEqualTo(launch.isSuccess)
        assertThat(uiModel.patchImageUrl).isEqualTo(launch.patchImageUrl)
        assertThat(uiModel.details).isEqualTo(launch.details)
        assertThat(uiModel.flickrImages).containsExactly("a", "b").inOrder()
    }

    @Test
    fun `toUiModel handles null missionName with default text`() {
        // GIVEN
        val launch = createLaunch(missionName = null)

        // WHEN
        val uiModel = launch.toUiModel()

        // THEN
        assertThat(uiModel.missionName).isEqualTo("Unknown Mission")
    }

    @Test
    fun `toUiModel handles null rocketName with default text`() {
        // GIVEN
        val launch = createLaunch(rocketName = null)

        // WHEN
        val uiModel = launch.toUiModel()

        // THEN
        assertThat(uiModel.rocketName).isEqualTo("Unknown Rocket")
    }

    @Test
    fun `toUiModel converts List to ImmutableList`() {
        // GIVEN
        val standardList = ArrayList<String>()
        standardList.add("image1")
        val launch = createLaunch(flickrImages = standardList)

        // WHEN
        val uiModel = launch.toUiModel()

        // THEN
        assertThat(uiModel.flickrImages).isInstanceOf(kotlinx.collections.immutable.ImmutableList::class.java)
        assertThat(uiModel.flickrImages).hasSize(1)
        assertThat(uiModel.flickrImages[0]).isEqualTo("image1")
    }

    @Test
    fun `toUiModel handles empty image list`() {
        // GIVEN
        val launch = createLaunch(flickrImages = emptyList())

        // WHEN
        val uiModel = launch.toUiModel()

        // THEN
        assertThat(uiModel.flickrImages).isEmpty()
    }
}
