package com.example.okey101

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.okey101.data.AppDatabase
import com.example.okey101.data.Game
import com.example.okey101.data.Player
import com.example.okey101.data.Score
import kotlinx.coroutines.launch
import com.google.android.material.textfield.TextInputLayout

class GameActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private var gameId: Long = -1
    private var players: List<Player> = emptyList()
    private var currentGame: Game? = null

    // Timers
    // Handlers are used for timing


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_game)

            gameId = intent.getLongExtra("GAME_ID", -1)
            if (gameId == -1L) {
                finish()
                return
            }

            database = AppDatabase.getDatabase(this)
            loadGameData()
        } catch (e: Exception) {
             e.printStackTrace()
             Toast.makeText(this, "GameActivity Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
             // Try to show dialog if possible
             /*
             androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Hata GameActivity onCreate")
                .setMessage(e.toString())
                .setPositiveButton("Tamam") { _, _ -> finish() }
                .show()
             */
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun loadGameData() {
        lifecycleScope.launch {
            currentGame = database.gameDao().getGame(gameId)
            players = database.playerDao().getPlayersForGameSync(gameId)
            
            setupButtons()
            if (currentGame?.isFinished == true) {
                disableInputs()
            }
            
            updateInputHints()
            updateLeaderboardUI()
        }
    }



    private fun updateInputHints() {
        val tilP1 = findViewById<TextInputLayout>(R.id.tilScoreP1)
        val tilP2 = findViewById<TextInputLayout>(R.id.tilScoreP2)
        val tilP3 = findViewById<TextInputLayout>(R.id.tilScoreP3)
        val tilP4 = findViewById<TextInputLayout>(R.id.tilScoreP4)

        if (players.size >= 4) {
            tilP1.hint = players[0].name
            tilP2.hint = players[1].name
            tilP3.hint = players[2].name
            tilP4.hint = players[3].name
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnAddRound).setOnClickListener {
            addRoundScores()
        }

        findViewById<Button>(R.id.btnFinishGame).setOnClickListener {
            finishGame()
        }
        
        findViewById<Button>(R.id.btnPunishmentWheel).setOnClickListener {
             // Loser is now the one with the HIGHEST score
             val loser = players.maxByOrNull { it.totalScore }
             val loserName = loser?.name ?: "Bilinmiyor"
             PunishmentWheelDialog(this, loserName).show()
        }

        findViewById<Button>(R.id.btnShare).setOnClickListener {
             // Winner is now the one with the LOWEST score
             val winner = players.minByOrNull { it.totalScore }
             val winnerName = winner?.name ?: "Bilinmiyor"
             val winnerScore = winner?.totalScore ?: 0
             ShareImageGenerator.shareGameResult(this, winnerName, winnerScore)
        }
        
        // History Button
        findViewById<android.view.View>(R.id.btnHistory).setOnClickListener {
            showHistoryDialog()
        }
    }
    
    private fun showHistoryDialog() {
        lifecycleScope.launch {
            // Fetch all scores for current players
            val allScores = mutableListOf<Score>()
            
            players.forEach { player ->
                val pScores = database.scoreDao().getScoresForPlayer(player.playerId)
                allScores.addAll(pScores)
            }
            
            if (allScores.isNotEmpty()) {
                ScoreHistoryDialog(this@GameActivity, players, allScores).show() 
            } else {
                Toast.makeText(this@GameActivity, "Henüz puan girilmedi.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun disableInputs() {
        findViewById<EditText>(R.id.etScoreP1).isEnabled = false
        findViewById<EditText>(R.id.etScoreP2).isEnabled = false
        findViewById<EditText>(R.id.etScoreP3).isEnabled = false
        findViewById<EditText>(R.id.etScoreP4).isEnabled = false
        findViewById<Button>(R.id.btnAddRound).isEnabled = false
        findViewById<Button>(R.id.btnFinishGame).isEnabled = false
        findViewById<Button>(R.id.btnAddRound).text = "Oyun Bitti"
        
        findViewById<android.view.View>(R.id.layoutEndGameButtons).visibility = android.view.View.VISIBLE
    }



    // Old timer code removed



    private fun addRoundScores() {
        val etP1 = findViewById<EditText>(R.id.etScoreP1)
        val etP2 = findViewById<EditText>(R.id.etScoreP2)
        val etP3 = findViewById<EditText>(R.id.etScoreP3)
        val etP4 = findViewById<EditText>(R.id.etScoreP4)

        val s1 = etP1.text.toString().toIntOrNull() ?: 0
        val s2 = etP2.text.toString().toIntOrNull() ?: 0
        val s3 = etP3.text.toString().toIntOrNull() ?: 0
        val s4 = etP4.text.toString().toIntOrNull() ?: 0

        lifecycleScope.launch {
            if (players.size < 4) {
                 Toast.makeText(this@GameActivity, "Oyuncu verileri yükleniyor, lütfen bekleyiniz...", Toast.LENGTH_SHORT).show()
                 return@launch
            }

            // Determine round number (count scores for p1 + 1)
            val p1Scores = database.scoreDao().getScoresForPlayer(players[0].playerId)
            val nextRound = p1Scores.size + 1

            database.scoreDao().insertScore(Score(playerId = players[0].playerId, roundNumber = nextRound, scoreValue = s1))
            database.scoreDao().insertScore(Score(playerId = players[1].playerId, roundNumber = nextRound, scoreValue = s2))
            database.scoreDao().insertScore(Score(playerId = players[2].playerId, roundNumber = nextRound, scoreValue = s3))
            database.scoreDao().insertScore(Score(playerId = players[3].playerId, roundNumber = nextRound, scoreValue = s4))

            // Update total scores
            updatePlayerScores()
            
            // Clear inputs
            etP1.text.clear()
            etP2.text.clear()
            etP3.text.clear()
            etP4.text.clear()
            
            updateLeaderboardUI()
        }
    }

    private suspend fun updatePlayerScores() {
        players.forEach { player ->
            val total = database.scoreDao().calculateTotalScore(player.playerId) ?: 0
            val updatedPlayer = player.copy(totalScore = total)
            database.playerDao().updatePlayer(updatedPlayer)
        }
        // Refresh local list
        players = database.playerDao().getPlayersForGameSync(gameId)
    }

    private fun updateLeaderboardUI() {
        val tvP1 = findViewById<TextView>(R.id.tvScoreP1)
        val tvP2 = findViewById<TextView>(R.id.tvScoreP2)
        val tvP3 = findViewById<TextView>(R.id.tvScoreP3)
        val tvP4 = findViewById<TextView>(R.id.tvScoreP4)
        
        // Sort: Lowest score first (Ascending)
        val sortedPlayers = players.sortedBy { it.totalScore }
        
        // Reset texts if needed or just overwrite them
        val textViews = listOf(tvP1, tvP2, tvP3, tvP4)
        
        var currentRank = 1
        for (i in sortedPlayers.indices) {
            val player = sortedPlayers[i]
            
            // Determine rank
            if (i > 0) {
                 val prevPlayer = sortedPlayers[i-1]
                 if (player.totalScore != prevPlayer.totalScore) {
                     currentRank = i + 1
                 }
            } else {
                currentRank = 1
            }

            if (i < textViews.size) {
                 val tv = textViews[i]
                 tv.text = "$currentRank. ${player.name}: ${player.totalScore}"
                 
                 // Highlight ALL winners (Rank 1)
                 if (currentRank == 1) {
                     tv.setTextColor(getColor(R.color.gold_accent))
                     tv.setTypeface(null, android.graphics.Typeface.BOLD)
                     
                     // Add Star Icon
                     val star = getDrawable(android.R.drawable.star_on)
                     star?.setTint(getColor(R.color.gold_accent))
                     tv.setCompoundDrawablesWithIntrinsicBounds(star, null, null, null)
                     tv.compoundDrawablePadding = 16
                 } else {
                     tv.setTextColor(getColor(R.color.white)) // or text_primary
                     tv.setTypeface(null, android.graphics.Typeface.NORMAL)
                     tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                 }
            }
        }
    }

    private fun finishGame() {
        lifecycleScope.launch {
            currentGame?.let {
                val updatedGame = it.copy(isFinished = true)
                database.gameDao().updateGame(updatedGame)
                
                 runOnUiThread {
                    Toast.makeText(applicationContext, getString(R.string.game_saved), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
