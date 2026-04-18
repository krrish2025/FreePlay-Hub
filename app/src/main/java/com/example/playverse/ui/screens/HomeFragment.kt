package com.example.playverse.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playverse.R
import com.example.playverse.data.api.RetrofitClient
import com.example.playverse.data.local.AppDatabase
import com.example.playverse.data.repository.GameRepository
import com.example.playverse.databinding.FragmentHomeBinding
import com.example.playverse.ui.components.GameAdapter
import com.example.playverse.viewmodel.GameViewModel
import com.example.playverse.viewmodel.GameViewModelFactory
import kotlinx.coroutines.launch

import com.example.playverse.ui.components.MainHomeAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels {
        val api = RetrofitClient.instance
        val dao = AppDatabase.getInstance(requireContext()).gameDao()
        GameViewModelFactory(GameRepository(api, dao))
    }

    private lateinit var mainAdapter: MainHomeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchAllGames()
    }

    private fun setupRecyclerView() {
        mainAdapter = MainHomeAdapter(
            onGameClick = { game -> navigateToDetail(game.id) },
            onCategoryClick = { category ->
                if (category != null) {
                    viewModel.fetchGamesByCategory(category)
                } else {
                    viewModel.fetchAllGames()
                }
            }
        )
        binding.rvMain.adapter = mainAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.games.collect { state ->
                    when (state) {
                        is GameViewModel.UiState.Loading -> {
                            binding.skeletonLayout.visibility = View.VISIBLE
                            binding.rvMain.visibility = View.GONE
                        }
                        is GameViewModel.UiState.Success -> {
                            binding.skeletonLayout.visibility = View.GONE
                            binding.rvMain.visibility = View.VISIBLE
                            val trending = state.data.shuffled().take(8)
                            mainAdapter.submitData(state.data, trending)
                        }
                        is GameViewModel.UiState.Error -> {
                            binding.skeletonLayout.visibility = View.GONE
                            binding.rvMain.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun navigateToDetail(gameId: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToGameDetailFragment(gameId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
