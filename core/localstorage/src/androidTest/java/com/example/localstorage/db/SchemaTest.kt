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
        // This test fails if the 'LaunchEntity' definition does not match exactly
        // with the schema defined in 'schemas/..../1.json'.
        // It is useful to prevent accidental changes to Entities without updating the DB version.
        val db = helper.createDatabase("test-db", 1)
        db.close()
    }

    @Test
    fun testMigration1To2() {
        val dbName = "migration-test"

        // 1. CREATE DATABASE VERSION 1
        // Room uses the 'schemas/1.json' file to recreate the old database structure.
        val dbV1 = helper.createDatabase(dbName, 1).apply {

            // 2. INSERT OLD DATA (Simulate a user with the old app version)
            // We must use raw SQL because the Kotlin 'LaunchEntity' class is already updated to v2.
            // Note: Ensure column names and order match the v1 schema exactly!
            // Be careful with TypeConverters (dates and lists must be inserted as Strings).
            execSQL(
                """
                INSERT INTO launches (id, missionName, launchDate, isSuccess, rocketId, rocketName, details, patchImageUrl, webcastUrl, articleUrl, flickrImages) 
                VALUES ('id-123', 'Mission V1', '2022-01-01T10:00:00Z', 1, 'r-1', 'Falcon', 'Details', NULL, NULL, NULL, '[]')
                """.trimIndent()
            )
            close()
        }

        // 3. RUN MIGRATION TO VERSION 2 AND VALIDATE
        // Room opens the v1 DB, applies the manual 'MIGRATION_1_2' strategy,
        // and verifies if the final result matches 'schemas/2.json'.
        val dbV2 = helper.runMigrationsAndValidate(dbName, 2, true, MIGRATION_1_2)

        // 4. VERIFY DATA INTEGRITY
        // We can now query the v2 database.
        val cursor = dbV2.query("SELECT * FROM launches WHERE id = ?", arrayOf("id-123"))
        assertThat(cursor.moveToFirst()).isTrue()

        // Verify old data is preserved correctly
        val nameIndex = cursor.getColumnIndex("missionName")
        val name = cursor.getString(nameIndex)
        assertThat(name).isEqualTo("Mission V1")

        // Verify the new column was added (userNotes)
        val notesIndex = cursor.getColumnIndex("userNotes")

        // It must exist (index != -1) and must be null (default value for existing rows)
        assertThat(notesIndex != -1).isTrue()
        assertThat(cursor.isNull(notesIndex)).isTrue()

        cursor.close()
    }
}
