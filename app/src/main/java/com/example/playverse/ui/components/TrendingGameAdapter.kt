package com.example.playverse.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playverse.data.model.Game
import com.example.playverse.databinding.ItemGameTrendingBinding

class TrendingGameAdapter(private val onGameClick: (Game) -> Unit) :
    ListAdapter<Game, TrendingGameAdapter.TrendingViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = ItemGameTrendingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrendingViewHolder(private val binding: ItemGameTrendingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game) {
            binding.gameTitle.text = game.title
            binding.gameGenre.text = game.genre

            Glide.with(binding.gameThumbnail.context)
                .load(game.thumbnail)
                .centerCrop()
                .into(binding.gameThumbnail)

            binding.root.setOnClickListener { onGameClick(game) }
        }
    }

    class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean = oldItem == newItem
    }
}