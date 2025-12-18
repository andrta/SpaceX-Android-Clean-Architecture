package com.example.data.datasources

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.example.data.GetPastLaunchesQuery
import com.example.data.datasource.GraphQlLaunchDataSource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.UUID

class GraphQlLaunchDataSourceTest {
    private lateinit var dataSource: GraphQlLaunchDataSource
    private val apolloClient: ApolloClient = mockk()
    private val apolloCall: ApolloCall<GetPastLaunchesQuery.Data> = mockk()

    @Before
    fun setup() {
        dataSource = GraphQlLaunchDataSource(apolloClient)
    }

    @Test
    fun `GIVEN success response WHEN getLastLaunches THEN returns mapped domain objects`() =
        runTest {
            // GIVEN
            val mockData = GetPastLaunchesQuery.Data(
                launchesPast = listOf(
                    GetPastLaunchesQuery.LaunchesPast(
                        id = "1",
                        mission_name = "Mission 1",
                        launch_date_utc = "2022-01-01T10:00:00.000Z",
                        launch_success = true,
                        details = "Details",
                        rocket = GetPastLaunchesQuery.Rocket(
                            rocket_name = "Falcon 9",
                            rocket = GetPastLaunchesQuery.Rocket1("r1")
                        ),
                        links = GetPastLaunchesQuery.Links(
                            mission_patch_small = null,
                            flickr_images = emptyList(),
                            video_link = null,
                            article_link = null,
                            wikipedia = null
                        )
                    )
                )
            )

            val realResponse = ApolloResponse.Builder(
                operation = GetPastLaunchesQuery(),
                requestUuid = UUID.randomUUID(),
                data = mockData
            ).build()

            every { apolloClient.query(GetPastLaunchesQuery()) } returns apolloCall
            coEvery { apolloCall.execute() } returns realResponse

            // WHEN
            val result = dataSource.getLastLaunches()

            // THEN
            assertThat(result).hasSize(1)
            assertThat(result[0].missionName).isEqualTo("Mission 1")
        }

    @Test
    fun `GIVEN null data response WHEN getLastLaunches THEN returns empty list`() = runTest {
        // GIVEN
        val realResponse = ApolloResponse.Builder(
            operation = GetPastLaunchesQuery(),
            requestUuid = UUID.randomUUID(),
            data = null
        ).build()

        every { apolloClient.query(GetPastLaunchesQuery()) } returns apolloCall
        coEvery { apolloCall.execute() } returns realResponse

        // WHEN
        val result = dataSource.getLastLaunches()

        // THEN
        assertThat(result).isEmpty()
    }

    @Test(expected = IOException::class)
    fun `GIVEN network error WHEN getLastLaunches THEN throws exception`() = runTest {
        // GIVEN
        every { apolloClient.query(any<GetPastLaunchesQuery>()) } returns apolloCall
        coEvery { apolloCall.execute() } throws IOException("Network Error")

        // WHEN
        dataSource.getLastLaunches()
    }
}
