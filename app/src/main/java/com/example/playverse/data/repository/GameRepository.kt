package com.example.playverse.data.repository

import com.example.playverse.data.api.FreeToGameApi
import com.example.playverse.data.local.CachedGame
import com.example.playverse.data.local.FavoriteGame
import com.example.playverse.data.local.GameDao
import com.example.playverse.data.model.Game
import com.example.playverse.data.model.GameDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

class GameRepository(
    private val api: FreeToGameApi,
    private val dao: GameDao
) {
    // Remote
    suspend fun getAllGamesRemote(): Response<List<Game>> = api.getAllGames()
    
    suspend fun getGamesByCategory(category: String): Response<List<Game>> = 
        api.getGamesByCategory(category)
        
    suspend fun getGamesByPlatform(platform: String): Response<List<Game>> = 
        api.getGamesByPlatform(platform)
        
    suspend fun getGamesSortedBy(sortBy: String): Response<List<Game>> = 
        api.getGamesSortedBy(sortBy)
        
    suspend fun getGameDetails(id: Int): Response<GameDetail> = api.getGameDetails(id)

    // Local DB - Favorites
    fun getFavorites(): Flow<List<FavoriteGame>> = dao.getAllFavorites()
    
    suspend fun addFavorite(game: FavoriteGame) = dao.insertFavorite(game)
    
    suspend fun removeFavorite(game: FavoriteGame) = dao.deleteFavorite(game)
    
    fun isFavorite(id: Int): Flow<Boolean> = dao.isFavorite(id)

    // Local DB - Caching
    fun getCachedGames(): Flow<List<Game>> = dao.getAllCachedGames().map { cachedList ->
        cachedList.map { 
            Game(
                id = it.id,
                title = it.title,
                thumbnail = it.thumbnail,
                shortDescription = it.shortDescription,
                gameUrl = "",
                genre = it.genre,
                platform = it.platform,
                publisher = "",
                developer = "",
                releaseDate = "",
                profileUrl = ""
            )
        }
    }

    suspend fun refreshCache(games: List<Game>) {
        dao.clearCache()
        val cachedGames = games.map { 
            CachedGame(
                id = it.id,
                title = it.title,
                thumbnail = it.thumbnail,
                shortDescription = it.shortDescription,
                genre = it.genre,
                platform = it.platform
            )
        }
        dao.insertGames(cachedGames)
    }
}
