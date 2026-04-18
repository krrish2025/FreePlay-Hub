package com.example.playverse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playverse.data.local.FavoriteGame
import com.example.playverse.data.model.Game
import com.example.playverse.data.model.GameDetail
import com.example.playverse.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _games = MutableStateFlow<UiState<List<Game>>>(UiState.Loading)
    val games: StateFlow<UiState<List<Game>>> = _games.asStateFlow()

    private val _gameDetail = MutableStateFlow<UiState<GameDetail>>(UiState.Loading)
    val gameDetail: StateFlow<UiState<GameDetail>> = _gameDetail.asStateFlow()

    val favorites: StateFlow<List<FavoriteGame>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchAllGames() {
        viewModelScope.launch {
            // First, show cached data if available
            repository.getCachedGames().collect { cachedGames ->
                if (cachedGames.isNotEmpty() && _games.value is UiState.Loading) {
                    _games.value = UiState.Success(cachedGames)
                }
            }
        }

        viewModelScope.launch {
            try {
                val response = repository.getAllGamesRemote()
                if (response.isSuccessful) {
                    val games = response.body() ?: emptyList()
                    _games.value = UiState.Success(games)
                    repository.refreshCache(games) // Save to local DB
                } else {
                    if (_games.value !is UiState.Success) {
                        _games.value = UiState.Error(response.message())
                    }
                }
            } catch (e: Exception) {
                if (_games.value !is UiState.Success) {
                    _games.value = UiState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }

    fun fetchGamesByCategory(category: String) {
        viewModelScope.launch {
            _games.value = UiState.Loading
            try {
                val response = repository.getGamesByCategory(category)
                if (response.isSuccessful) {
                    _games.value = UiState.Success(response.body() ?: emptyList())
                } else {
                    _games.value = UiState.Error(response.message())
                }
            } catch (e: Exception) {
                _games.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun fetchGameDetails(id: Int) {
        viewModelScope.launch {
            _gameDetail.value = UiState.Loading
            try {
                val response = repository.getGameDetails(id)
                if (response.isSuccessful && response.body() != null) {
                    _gameDetail.value = UiState.Success(response.body()!!)
                } else {
                    _gameDetail.value = UiState.Error(response.message())
                }
            } catch (e: Exception) {
                _gameDetail.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun toggleFavorite(game: Game) {
        viewModelScope.launch {
            val favorite = FavoriteGame(
                id = game.id,
                title = game.title,
                thumbnail = game.thumbnail,
                genre = game.genre,
                platform = game.platform
            )
            // Logic to check if exists and toggle
            // For simplicity, we can use a separate flow or check repository
        }
    }
    
    fun addFavorite(favorite: FavoriteGame) = viewModelScope.launch {
        repository.addFavorite(favorite)
    }
    
    fun removeFavorite(favorite: FavoriteGame) = viewModelScope.launch {
        repository.removeFavorite(favorite)
    }

    fun isFavorite(id: Int) = repository.isFavorite(id)

    sealed class UiState<out T> {
        object Loading : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }
}

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
