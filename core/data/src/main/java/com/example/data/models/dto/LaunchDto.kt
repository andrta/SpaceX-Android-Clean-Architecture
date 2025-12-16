package com.example.data.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LaunchDto(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("date_utc")
    val dateUtc: String,

    @SerialName("rocket")
    val rocketId: String,

    @SerialName("success")
    val success: Boolean? = null,

    @SerialName("details")
    val details: String? = null,

    @SerialName("links")
    val links: LinksDto? = null
)

@Serializable
data class LinksDto(
    @SerialName("patch")
    val patch: PatchDto? = null,

    @SerialName("webcast")
    val webcast: String? = null,

    @SerialName("article")
    val article: String? = null,

    @SerialName("wikipedia")
    val wikipedia: String? = null,

    @SerialName("flickr")
    val flickr: FlickrDto? = null
)

@Serializable
data class PatchDto(
    @SerialName("small")
    val small: String? = null,

    @SerialName("large")
    val large: String? = null
)

@Serializable
data class FlickrDto(
    @SerialName("original")
    val original: List<String> = emptyList(),

    @SerialName("small")
    val small: List<String> = emptyList(),
)
