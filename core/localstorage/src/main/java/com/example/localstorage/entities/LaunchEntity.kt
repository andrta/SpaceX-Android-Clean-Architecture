package com.example.localstorage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "launches")
data class LaunchEntity(
    @PrimaryKey
    val id: String,
    val missionName: String,
    val launchDate: ZonedDateTime,
    val isSuccess: Boolean?,

    val rocketId: String,
    val rocketName: String,

    val patchImageUrl: String?,
    val webcastUrl: String?,
    val articleUrl: String?,
    val wikipediaUrl: String?,

    val details: String?,
    val flickrImages: List<String>,

    val userNotes: String? = null,
    val isFavorite: Boolean = false,
)
