package com.example.localstorage.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Define the migration strategy from version 1 to version 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Execute the raw SQL command to add the new column.
        // Since Room uses SQLite, the SQL type for a String field is TEXT.
        db.execSQL("ALTER TABLE launches ADD COLUMN userNotes TEXT")
    }
}
