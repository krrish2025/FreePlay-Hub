package com.example.playverse.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playverse.R
import com.example.playverse.data.api.RetrofitClient
import com.example.playverse.data.local.AppDatabase
import com.example.playverse.data.local.FavoriteGame
import com.example.playverse.data.model.GameDetail
import com.example.playverse.data.repository.GameRepository
import com.example.playverse.databinding.FragmentGameDetailBinding
import com.example.playverse.viewmodel.GameViewModel
import com.example.playverse.viewmodel.GameViewModelFactory
import kotlinx.coroutines.launch

class GameDetailFragment : Fragment(R.layout.fragment_game_detail) {

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!

    private val args: GameDetailFragmentArgs by navArgs()

    private val viewModel: GameViewModel by viewModels {
        val api = RetrofitClient.instance
        val dao = AppDatabase.getInstance(requireContext()).gameDao()
        GameViewModelFactory(GameRepository(api, dao))
    }

    private var currentGame: GameDetail? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGameDetailBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        observeViewModel()
        viewModel.fetchGameDetails(args.gameId)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameDetail.collect { state ->
                    when (state) {
                        is GameViewModel.UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is GameViewModel.UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val game = state.data
                            currentGame = game
                            updateUI(game)
                        }
                        is GameViewModel.UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite(args.gameId).collect { isFav ->
                    if (isFav) {
                        binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                    } else {
                        binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                    }

                    binding.btnFavorite.setOnClickListener {
                        currentGame?.let { game ->
                            val fav = FavoriteGame(
                                id = game.id,
                                title = game.title,
                                thumbnail = game.thumbnail,
                                genre = game.genre,
                                platform = game.platform
                            )
                            if (isFav) {
                                viewModel.removeFavorite(fav)
                            } else {
                                viewModel.addFavorite(fav)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(game: GameDetail) {
        binding.tvTitle.text = game.title
        binding.tvGenrePlatform.text = "${game.genre} | ${game.platform}"
        binding.tvDescription.text = game.description

        Glide.with(requireContext())
            .load(game.thumbnail)
            .into(binding.ivBanner)

        binding.btnPlayNow.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(game.gameUrl))
            startContext(intent)
        }
    }

    private fun startContext(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
