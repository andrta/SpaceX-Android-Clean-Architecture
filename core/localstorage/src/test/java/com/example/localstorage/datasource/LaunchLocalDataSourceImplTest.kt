package com.example.localstorage.datasource

import com.example.domain.models.Launch
import com.example.localstorage.dao.LaunchDao
import com.example.localstorage.entities.LaunchEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class LaunchLocalDataSourceImplTest {
    private lateinit var dataSource: LaunchLocalDataSourceImpl
    private val dao: LaunchDao = mockk(relaxed = true)

    @Before
    fun setup() {
        dataSource = LaunchLocalDataSourceImpl(dao)
    }

    @Test
    fun `GIVEN dao returns entities WHEN getLaunches THEN maps to domain`() = runTest {
        // GIVEN
        val entity = createMockEntity()
        every { dao.getAllLaunches() } returns flowOf(listOf(entity))

        // WHEN
        val result = dataSource.getLaunches().first()

        // THEN
        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(entity.id)
        assertThat(result[0].missionName).isEqualTo(entity.missionName)
    }

    @Test
    fun `GIVEN launches WHEN saveLaunches THEN calls dao insert with entities`() = runTest {
        // GIVEN
        val launch = createMockLaunch()
        val slot = slot<List<LaunchEntity>>()

        // WHEN
        dataSource.saveLaunches(listOf(launch))

        // THEN
        coVerify { dao.insertAll(capture(slot)) }
        assertThat(slot.captured).hasSize(1)
        assertThat(slot.captured[0].id).isEqualTo(launch.id)
    }

    @Test
    fun `GIVEN id WHEN getLaunchById THEN returns mapped launch`() = runTest {
        // GIVEN
        val entity = createMockEntity()
        coEvery { dao.getLaunchById("1") } returns entity

        // WHEN
        val result = dataSource.getLaunchById("1")

        // THEN
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo("1")
    }

    private fun createMockEntity() = LaunchEntity(
        id = "1",
        missionName = "Test Mission",
        launchDate = ZonedDateTime.now(),
        isSuccess = true,
        rocketId = "r1",
        rocketName = "Falcon",
        details = null,
        patchImageUrl = null,
        webcastUrl = null,
        articleUrl = null,
        wikipediaUrl = null,
        flickrImages = emptyList(),
        userNotes = null
    )

    private fun createMockLaunch() = Launch(
        id = "1",
        missionName = "Test Mission",
        launchDate = ZonedDateTime.now(),
        isSuccess = true,
        rocketId = "r1",
        rocketName = "Falcon",
        details = null,
        patchImageUrl = null,
        webcastUrl = null,
        articleUrl = null,
        wikipediaUrl = null,
        flickrImages = emptyList()
    )
}
