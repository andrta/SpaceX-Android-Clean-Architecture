package com.example.localstorage.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RoomTypeConverters {
    @TypeConverter
    fun fromTimestamp(value: String?): ZonedDateTime? {
        return value?.let { ZonedDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: ZonedDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }

    @TypeConverter
    fun fromList(value: String): List<String> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun toList(list: List<String>): String {
        return Json.encodeToString(list)
    }
}
