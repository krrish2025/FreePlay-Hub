package com.example.playverse.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(game: FavoriteGame)

    @Delete
    suspend fun deleteFavorite(game: FavoriteGame)

    @Query("SELECT * FROM favorite_games ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteGame>>

    @Query("SELECT EXISTS(SELECT * FROM favorite_games WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Query("SELECT * FROM favorite_games WHERE id = :id")
    suspend fun getFavoriteById(id: Int): FavoriteGame?

    // Caching
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<CachedGame>)

    @Query("SELECT * FROM cached_games")
    fun getAllCachedGames(): Flow<List<CachedGame>>

    @Query("DELETE FROM cached_games")
    suspend fun clearCache()
}
