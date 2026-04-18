package com.example.playverse.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playverse.data.api.RetrofitClient
import com.example.playverse.data.local.AppDatabase
import com.example.playverse.data.model.Game
import com.example.playverse.data.repository.GameRepository
import com.example.playverse.databinding.FragmentFavoritesBinding
import com.example.playverse.ui.components.GameAdapter
import com.example.playverse.viewmodel.GameViewModel
import com.example.playverse.viewmodel.GameViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory(
            GameRepository(
                RetrofitClient.instance,
                AppDatabase.getInstance(requireContext()).gameDao()
            )
        )
    }

    private lateinit var gameAdapter: GameAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        gameAdapter = GameAdapter { favorite ->
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToGameDetailFragment(favorite.id)
            findNavController().navigate(action)
        }
        binding.rvFavorites.adapter = gameAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collectLatest { favorites ->
                if (favorites.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvFavorites.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvFavorites.visibility = View.VISIBLE
                    
                    // Convert FavoriteGame to Game for the adapter
                    val games = favorites.map { favorite ->
                        Game(
                            id = favorite.id,
                            title = favorite.title,
                            thumbnail = favorite.thumbnail,
                            shortDescription = "",
                            gameUrl = "",
                            genre = favorite.genre,
                            platform = favorite.platform,
                            publisher = "",
                            developer = "",
                            releaseDate = "",
                            profileUrl = ""
                        )
                    }
                    gameAdapter.submitList(games)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
