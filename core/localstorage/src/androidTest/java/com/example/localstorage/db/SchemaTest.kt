package com.example.localstorage.db

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SchemaTest {
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SpaceXDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun validateSchemaVersion1() {
        val db = helper.createDatabase("test-db", 1)
        db.close()
    }

    @Test
    fun testMigration1To2() {
        val dbName = "migration-test-1-2"

        val dbV1 = helper.createDatabase(dbName, 1).apply {
            execSQL(
                """
                INSERT INTO launches (id, missionName, launchDate, isSuccess, rocketId, rocketName, details, patchImageUrl, webcastUrl, articleUrl, flickrImages) 
                VALUES ('id-123', 'Mission V1', '2022-01-01T10:00:00Z', 1, 'r-1', 'Falcon', 'Details', NULL, NULL, NULL, '[]')
                """.trimIndent()
            )
            close()
        }

        val dbV2 = helper.runMigrationsAndValidate(dbName, 2, true, MIGRATION_1_2)

        val cursor = dbV2.query("SELECT * FROM launches WHERE id = ?", arrayOf("id-123"))
        assertThat(cursor.moveToFirst()).isTrue()

        val nameIndex = cursor.getColumnIndex("missionName")
        assertThat(cursor.getString(nameIndex)).isEqualTo("Mission V1")

        val notesIndex = cursor.getColumnIndex("userNotes")
        assertThat(notesIndex != -1).isTrue()
        assertThat(cursor.isNull(notesIndex)).isTrue()

        cursor.close()
    }

    @Test
    fun testMigration2To3() {
        val dbName = "migration-test-2-3"

        val dbV2 = helper.createDatabase(dbName, 2).apply {
            execSQL(
                """
                INSERT INTO launches (id, missionName, launchDate, isSuccess, rocketId, rocketName, details, patchImageUrl, webcastUrl, articleUrl, flickrImages, userNotes) 
                VALUES ('id-456', 'Mission V2', '2023-01-01T10:00:00Z', 1, 'r-2', 'Falcon Heavy', 'Details V2', NULL, NULL, NULL, '[]', 'Some notes')
                """.trimIndent()
            )
            close()
        }

        val dbV3 = helper.runMigrationsAndValidate(dbName, 3, true, MIGRATION_2_3)

        val cursor = dbV3.query("SELECT * FROM launches WHERE id = ?", arrayOf("id-456"))
        assertThat(cursor.moveToFirst()).isTrue()

        val notesIndex = cursor.getColumnIndex("userNotes")
        assertThat(cursor.getString(notesIndex)).isEqualTo("Some notes")

        val favoriteIndex = cursor.getColumnIndex("isFavorite")
        assertThat(favoriteIndex != -1).isTrue()
        assertThat(cursor.getInt(favoriteIndex)).isEqualTo(0)

        cursor.close()
    }
}
