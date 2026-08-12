package com.example.okey101

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.okey101.data.Game
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameAdapter(
    private val onClick: (Game) -> Unit,
    private val onDeleteClick: (Game) -> Unit
) : ListAdapter<Game, GameAdapter.GameViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view, onClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = getItem(position)
        holder.bind(game)
    }

    class GameViewHolder(
        itemView: View,
        val onClick: (Game) -> Unit,
        val onDeleteClick: (Game) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvGameDate)
        private val tvHost: TextView = itemView.findViewById(R.id.tvHostName)
        private val btnDelete: View = itemView.findViewById(R.id.btnDeleteGame)

        fun bind(game: Game) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(game.date))
            tvHost.text = "Ev Sahibi: ${game.hostName}"
            itemView.setOnClickListener { onClick(game) }
            btnDelete.setOnClickListener { onDeleteClick(game) }
        }
    }

    class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.gameId == newItem.gameId
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }
}
