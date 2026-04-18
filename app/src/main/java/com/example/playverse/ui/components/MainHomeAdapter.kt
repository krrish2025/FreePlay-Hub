package com.example.playverse.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playverse.data.model.Game
import com.example.playverse.databinding.LayoutHomeHeaderBinding
import com.example.playverse.databinding.LayoutHomeTrendingBinding
import com.example.playverse.databinding.ItemGameBinding

class MainHomeAdapter(
    private val onGameClick: (Game) -> Unit,
    private val onCategoryClick: (String?) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var allGames: List<Game> = emptyList()
    private var trendingGames: List<Game> = emptyList()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TRENDING = 1
        private const val TYPE_GAME = 2
    }

    fun submitData(all: List<Game>, trending: List<Game>) {
        this.allGames = all
        this.trendingGames = trending
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            1 -> TYPE_TRENDING
            else -> TYPE_GAME
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(LayoutHomeHeaderBinding.inflate(inflater, parent, false))
            TYPE_TRENDING -> TrendingViewHolder(LayoutHomeTrendingBinding.inflate(inflater, parent, false))
            else -> GameViewHolder(ItemGameBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind()
            is TrendingViewHolder -> holder.bind(trendingGames)
            is GameViewHolder -> holder.bind(allGames[position - 2])
        }
    }

    override fun getItemCount(): Int = allGames.size + 2

    inner class HeaderViewHolder(private val binding: LayoutHomeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
                val category = when (checkedIds.firstOrNull()) {
                    com.example.playverse.R.id.chip_shooter -> "shooter"
                    com.example.playverse.R.id.chip_mmo -> "mmorpg"
                    com.example.playverse.R.id.chip_strategy -> "strategy"
                    else -> null
                }
                onCategoryClick(category)
            }
        }
    }

    inner class TrendingViewHolder(private val binding: LayoutHomeTrendingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val adapter = TrendingGameAdapter(onGameClick)
        init {
            binding.rvTrending.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.rvTrending.adapter = adapter
            binding.rvTrending.clipToPadding = false
            binding.rvTrending.setPadding(0, 0, 40, 0)
        }
        fun bind(games: List<Game>) {
            adapter.submitList(games)
        }
    }

    inner class GameViewHolder(private val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game) {
            binding.gameTitle.text = game.title
            binding.gameGenre.text = game.genre
            binding.gamePlatform.text = game.platform
            com.bumptech.glide.Glide.with(binding.gameThumbnail.context)
                .load(game.thumbnail)
                .centerCrop()
                .into(binding.gameThumbnail)
            binding.root.setOnClickListener { onGameClick(game) }
        }
    }
}
