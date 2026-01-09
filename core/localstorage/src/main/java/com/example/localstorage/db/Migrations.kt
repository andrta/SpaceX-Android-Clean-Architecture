package com.example.localstorage.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE launches ADD COLUMN userNotes TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Create a new table with the desired schema (isSuccess is nullable, and isFavorite is added)
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `launches_new` (
                `id` TEXT NOT NULL, 
                `missionName` TEXT NOT NULL, 
                `launchDate` TEXT NOT NULL, 
                `isSuccess` INTEGER, 
                `rocketId` TEXT NOT NULL, 
                `rocketName` TEXT NOT NULL, 
                `patchImageUrl` TEXT, 
                `webcastUrl` TEXT, 
                `articleUrl` TEXT, 
                `wikipediaUrl` TEXT, 
                `details` TEXT, 
                `flickrImages` TEXT NOT NULL, 
                `userNotes` TEXT, 
                `isFavorite` INTEGER NOT NULL DEFAULT 0, 
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )

        // 2. Copy the data from the old table to the new table
        db.execSQL(
            """
            INSERT INTO launches_new (id, missionName, launchDate, isSuccess, rocketId, rocketName, patchImageUrl, webcastUrl, articleUrl, wikipediaUrl, details, flickrImages, userNotes)
            SELECT id, missionName, launchDate, isSuccess, rocketId, rocketName, patchImageUrl, webcastUrl, articleUrl, wikipediaUrl, details, flickrImages, userNotes
            FROM launches
            """.trimIndent()
        )

        // 3. Remove the old table
        db.execSQL("DROP TABLE launches")

        // 4. Rename the new table to the original name
        db.execSQL("ALTER TABLE launches_new RENAME TO launches")
    }
}
