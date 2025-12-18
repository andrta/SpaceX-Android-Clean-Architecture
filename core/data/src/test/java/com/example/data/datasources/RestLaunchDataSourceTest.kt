package com.example.data.datasources

import com.example.data.datasource.RestLaunchDataSource
import com.example.data.models.dto.LaunchDto
import com.example.data.models.dto.LinksDto
import com.example.data.models.dto.RocketDto
import com.example.data.services.SpaceXApiService
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

class RestLaunchDataSourceTest {
    private lateinit var dataSource: RestLaunchDataSource
    private val api: SpaceXApiService = mockk()

    @Before
    fun setup() {
        dataSource = RestLaunchDataSource(api)
    }

    @Test
    fun `GIVEN launches and successful rocket fetch WHEN getLastLaunches THEN combines data correctly`() =
        runTest {
            // GIVEN
            val launchDto = createMockLaunchDto(id = "l1", rocketId = "r1")
            coEvery { api.getLaunches() } returns listOf(launchDto)

            val rocketDto = RocketDto(id = "r1", name = "Falcon Heavy")
            coEvery { api.getRocket("r1") } returns rocketDto

            // WHEN
            val result = dataSource.getLastLaunches()

            // THEN
            assertThat(result).hasSize(1)
            assertThat(result[0].id).isEqualTo("l1")
            assertThat(result[0].rocketName).isEqualTo("Falcon Heavy")

            coVerify(exactly = 1) { api.getRocket("r1") }
        }

    @Test
    fun `GIVEN distinct rocket IDs WHEN getLastLaunches THEN calls rocket api once per ID`() =
        runTest {
            // GIVEN
            val launch1 = createMockLaunchDto(id = "l1", rocketId = "r1")
            val launch2 = createMockLaunchDto(id = "l2", rocketId = "r1")
            coEvery { api.getLaunches() } returns listOf(launch1, launch2)

            val rocketDto = RocketDto(id = "r1", name = "Falcon 9")
            coEvery { api.getRocket("r1") } returns rocketDto

            // WHEN
            val result = dataSource.getLastLaunches()

            // THEN
            assertThat(result).hasSize(2)
            coVerify(exactly = 1) { api.getRocket("r1") }
        }

    @Test
    fun `GIVEN rocket api fails WHEN getLastLaunches THEN uses fallback name`() = runTest {
        // GIVEN
        val launchDto = createMockLaunchDto(id = "l1", rocketId = "r_fail")
        coEvery { api.getLaunches() } returns listOf(launchDto)
        coEvery { api.getRocket("r_fail") } throws IOException("404 Not Found")

        // WHEN
        val result = dataSource.getLastLaunches()

        // THEN
        assertThat(result).hasSize(1)
        assertThat(result[0].rocketId).isEqualTo("r_fail")
        assertThat(result[0].rocketName).contains("Unknown Rocket Name")
    }

    @Test(expected = IOException::class)
    fun `GIVEN main launches api fails WHEN getLastLaunches THEN throws exception`() = runTest {
        // GIVEN
        coEvery { api.getLaunches() } throws IOException("No Internet")

        // WHEN
        dataSource.getLastLaunches()
    }

    private fun createMockLaunchDto(id: String, rocketId: String) = LaunchDto(
        id = id,
        name = "Mission $id",
        dateUtc = "2022-01-01T10:00:00.000Z",
        success = true,
        rocketId = rocketId,
        details = null,
        links = LinksDto(null, null, null, null, null)
    )
}
