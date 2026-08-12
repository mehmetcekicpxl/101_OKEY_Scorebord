package com.example.okey101

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class StatisticsAdapter : ListAdapter<PlayerStats, StatisticsAdapter.StatsViewHolder>(StatsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stat_player, parent, false)
        return StatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val item = getItem(position)
        // Pass the explicit rank calculated in Activity
        holder.bind(item, item.rank)
    }

    class StatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRank: TextView = itemView.findViewById(R.id.tvRank)
        private val tvName: TextView = itemView.findViewById(R.id.tvPlayerName)
        private val tvWins: TextView = itemView.findViewById(R.id.tvWins)
        private val tvGames: TextView = itemView.findViewById(R.id.tvGamesPlayed)
        private val tvPoints: TextView = itemView.findViewById(R.id.tvTotalPoints)
        private val ivCrown: ImageView = itemView.findViewById(R.id.ivCrown)

        fun bind(stats: PlayerStats, rank: Int) {
            tvRank.text = "#$rank"
            tvName.text = stats.name
            tvWins.text = "${stats.wins} Zafer"
            tvGames.text = "${stats.gamesPlayed} Oyun"
            tvPoints.text = "Toplam Puan: ${stats.totalScore}"

            // If rank is 1, show crown (even if multiple people are rank 1)
            if (rank == 1) {
                ivCrown.visibility = View.VISIBLE
                tvRank.setTextColor(itemView.context.getColor(R.color.gold_accent))
                tvName.setTextColor(itemView.context.getColor(R.color.gold_accent))
            } else {
                ivCrown.visibility = View.GONE
                tvRank.setTextColor(itemView.context.getColor(R.color.text_secondary))
                tvName.setTextColor(itemView.context.getColor(R.color.white))
            }
        }
    }

    class StatsDiffCallback : DiffUtil.ItemCallback<PlayerStats>() {
        override fun areItemsTheSame(oldItem: PlayerStats, newItem: PlayerStats): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PlayerStats, newItem: PlayerStats): Boolean {
            return oldItem == newItem
        }
    }
}
