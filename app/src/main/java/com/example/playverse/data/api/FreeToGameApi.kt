package com.example.playverse.data.api

import com.example.playverse.data.model.Game
import com.example.playverse.data.model.GameDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FreeToGameApi {

    @GET("games")
    suspend fun getAllGames(): Response<List<Game>>

    @GET("games")
    suspend fun getGamesByCategory(@Query("category") category: String): Response<List<Game>>

    @GET("games")
    suspend fun getGamesByPlatform(@Query("platform") platform: String): Response<List<Game>>

    @GET("games")
    suspend fun getGamesSortedBy(@Query("sort-by") sortBy: String): Response<List<Game>>

    @GET("game")
    suspend fun getGameDetails(@Query("id") id: Int): Response<GameDetail>
}
