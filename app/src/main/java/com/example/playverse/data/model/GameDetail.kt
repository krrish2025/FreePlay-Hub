package com.example.playverse.data.model

import com.google.gson.annotations.SerializedName

data class GameDetail(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val status: String,
    @SerializedName("short_description")
    val shortDescription: String,
    val description: String,
    @SerializedName("game_url")
    val gameUrl: String,
    val genre: String,
    val platform: String,
    val publisher: String,
    val developer: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("freetogame_profile_url")
    val profileUrl: String,
    val screenshots: List<Screenshot>,
    @SerializedName("minimum_system_requirements")
    val minimumSystemRequirements: MinimumSystemRequirements?
)

data class Screenshot(
    val id: Int,
    val image: String
)

data class MinimumSystemRequirements(
    val os: String?,
    val processor: String?,
    val memory: String?,
    val graphics: String?,
    val storage: String?
)
