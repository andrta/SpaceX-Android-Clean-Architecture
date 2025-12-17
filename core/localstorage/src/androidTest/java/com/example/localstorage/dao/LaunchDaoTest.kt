package com.example.localstorage.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.localstorage.db.SpaceXDatabase
import com.example.localstorage.entities.LaunchEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
@SmallTest
class LaunchDaoTest {
    private lateinit var database: SpaceXDatabase
    private lateinit var dao: LaunchDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SpaceXDatabase::class.java
        ).build()
        dao = database.launchDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndReadLaunch() = runTest {
        // GIVEN
        val entity = LaunchEntity(
            id = "test-id-1",
            missionName = "Test Mission",
            launchDate = ZonedDateTime.now(),
            isSuccess = true,
            rocketId = "falcon-9",
            rocketName = "Falcon 9",
            details = "Test details",
            patchImageUrl = null,
            webcastUrl = null,
            articleUrl = null,
            wikipediaUrl = null,
            flickrImages = listOf("img1.jpg", "img2.jpg") // Testiamo anche il converter della lista
        )

        // WHEN
        dao.insertAll(listOf(entity))

        // THEN
        val loadedList = dao.getAllLaunches().first()
        assertThat(loadedList).isNotEmpty()
        assertThat(loadedList.size).isEqualTo(1)
        val loadedEntity = loadedList[0]
        assertThat(loadedEntity.id).isEqualTo(entity.id)
        assertThat(loadedEntity.missionName).isEqualTo(entity.missionName)
        assertThat(loadedEntity.flickrImages.size).isEqualTo(2)
        assertThat(loadedEntity.flickrImages[0]).isEqualTo("img1.jpg")
    }
}
