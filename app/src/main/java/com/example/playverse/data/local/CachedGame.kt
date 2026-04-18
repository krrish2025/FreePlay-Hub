package com.example.playverse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_games")
data class CachedGame(
    @PrimaryKey
    val id: Int,
    val title: String,
    val thumbnail: String,
    val shortDescription: String,
    val genre: String,
    val platform: String,
    val isTrending: Boolean = false
)
