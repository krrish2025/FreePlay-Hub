package com.example.playverse.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playverse.R
import com.example.playverse.data.api.RetrofitClient
import com.example.playverse.data.local.AppDatabase
import com.example.playverse.data.repository.GameRepository
import com.example.playverse.databinding.FragmentSearchBinding
import com.example.playverse.ui.components.GameAdapter
import com.example.playverse.viewmodel.GameViewModel
import com.example.playverse.viewmodel.GameViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
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
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()
        
        viewModel.fetchAllGames()
    }

    private fun setupRecyclerView() {
        gameAdapter = GameAdapter { game ->
            val action = SearchFragmentDirections.actionSearchFragmentToGameDetailFragment(game.id)
            findNavController().navigate(action)
        }
        binding.rvSearchResults.adapter = gameAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                filterGames(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterGames(query: String) {
        val currentState = viewModel.games.value
        if (currentState is GameViewModel.UiState.Success) {
            val filteredList = if (query.isEmpty()) {
                currentState.data
            } else {
                currentState.data.filter { 
                    it.title.contains(query, ignoreCase = true) ||
                    it.genre.contains(query, ignoreCase = true)
                }
            }
            gameAdapter.submitList(filteredList)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.games.collectLatest { state ->
                when (state) {
                    is GameViewModel.UiState.Loading -> {
                        binding.skeletonSearch.visibility = View.VISIBLE
                        binding.rvSearchResults.visibility = View.GONE
                    }
                    is GameViewModel.UiState.Success -> {
                        binding.skeletonSearch.visibility = View.GONE
                        binding.rvSearchResults.visibility = View.VISIBLE
                        if (binding.etSearch.text.isNullOrEmpty()) {
                            gameAdapter.submitList(state.data)
                        }
                    }
                    is GameViewModel.UiState.Error -> {
                        binding.skeletonSearch.visibility = View.GONE
                        binding.rvSearchResults.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
