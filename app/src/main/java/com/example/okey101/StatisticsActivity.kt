package com.example.okey101

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.okey101.data.AppDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PlayerStats(
    val name: String,
    val gamesPlayed: Int,
    val wins: Int,
    val totalScore: Int,
    var rank: Int = 0 // Added rank field
)

class StatisticsActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var adapter: StatisticsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        database = AppDatabase.getDatabase(this)

        val rvStats = findViewById<RecyclerView>(R.id.rvStats)
        adapter = StatisticsAdapter()
        rvStats.layoutManager = LinearLayoutManager(this)
        rvStats.adapter = adapter

        calculateAndDisplayStats()
    }

    private fun calculateAndDisplayStats() {
        lifecycleScope.launch(Dispatchers.IO) {
            val allGames = database.gameDao().getAllGamesSync() 
            val allPlayers = database.playerDao().getAllPlayers()

            // Map: PlayerName -> Stats
            val statsMap = mutableMapOf<String, PlayerStats>()

            // 1. Group players by game to find winners
            val playersByGame = allPlayers.groupBy { it.gameId }

            playersByGame.forEach { (gameId, playersInGame) ->
                val game = allGames.find { it.gameId == gameId }
                
                if (game != null && game.isFinished) {
                    
                    // Find winner (Lowest Score)
                    val winnerScore = playersInGame.minOfOrNull { it.totalScore }

                    playersInGame.forEach { player ->
                        val currentStats = statsMap.getOrDefault(player.name, PlayerStats(player.name, 0, 0, 0))
                        
                        // Check if this player is ONE OF the winners (tied for lowest score)
                        val isWinner = (player.totalScore == winnerScore)
                        
                        statsMap[player.name] = currentStats.copy(
                            gamesPlayed = currentStats.gamesPlayed + 1,
                            wins = currentStats.wins + (if (isWinner) 1 else 0),
                            totalScore = currentStats.totalScore + player.totalScore
                        )
                    }
                }
            }

            // Order: Wins (Desc), Total Score (Ascending - lower is better)
            val sortedStats = statsMap.values.toList()
                .sortedWith(compareByDescending<PlayerStats> { it.wins }.thenBy { it.totalScore })
            
            // Assign Ranks (Handle Ties)
            var currentRank = 1
            for (i in sortedStats.indices) {
                if (i > 0) {
                    val prev = sortedStats[i-1]
                    val curr = sortedStats[i]
                    // If Win count AND Total Score are same -> Same Rank
                    if (curr.wins != prev.wins || curr.totalScore != prev.totalScore) {
                        currentRank = i + 1
                    }
                } else {
                    currentRank = 1
                }
                sortedStats[i].rank = currentRank
            }

            withContext(Dispatchers.Main) {
                adapter.submitList(sortedStats)
            }
        }
    }
}
