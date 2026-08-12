package com.example.okey101

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.okey101.data.Player
import com.example.okey101.data.Score

class ScoreHistoryDialog(
    context: Context,
    private val players: List<Player>,
    private val allScores: List<Score>
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_dialog_history)
        // Set width to match parent
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setupTable()
        
        findViewById<Button>(R.id.btnCloseHistory).setOnClickListener {
            dismiss()
        }
    }

    private fun setupTable() {
        val table = findViewById<TableLayout>(R.id.tableHistory)
        
        // Update headers to show current player names
        if (players.size >= 4) {
            findViewById<TextView>(R.id.tvHeaderP1).text = players[0].name
            findViewById<TextView>(R.id.tvHeaderP2).text = players[1].name
            findViewById<TextView>(R.id.tvHeaderP3).text = players[2].name
            findViewById<TextView>(R.id.tvHeaderP4).text = players[3].name
        }

        // 1. Group scores by round number
        // Map: RoundNumber -> Map<PlayerId, ScoreValue>
        val scoresByRound = mutableMapOf<Int, MutableMap<Long, Int>>()
        
        allScores.forEach { score ->
            if (!scoresByRound.containsKey(score.roundNumber)) {
                scoresByRound[score.roundNumber] = mutableMapOf()
            }
            scoresByRound[score.roundNumber]?.put(score.playerId, score.scoreValue)
        }

        // 2. Sort rounds (ascending)
        val sortedRoundNumbers = scoresByRound.keys.sorted()

        // 3. Create rows
        sortedRoundNumbers.forEach { roundNum ->
            val row = TableRow(context)
            row.setPadding(0, 16, 0, 16) // Add some vertical padding for readability
            
            // Round Number Column
            val tvRound = TextView(context).apply {
                text = roundNum.toString()
                setTextColor(context.getColor(R.color.white))
                gravity = android.view.Gravity.CENTER
                textSize = 16f
            }
            row.addView(tvRound)

            // Player Columns (Ordered by players list)
            players.forEach { player ->
                val scoreVal = scoresByRound[roundNum]?.get(player.playerId)
                // Use "-" if score missing for some reason
                val scoreText = scoreVal?.toString() ?: "-"
                
                val tvScore = TextView(context).apply {
                    text = scoreText
                    setTextColor(context.getColor(R.color.white))
                    gravity = android.view.Gravity.CENTER
                    textSize = 16f
                }
                row.addView(tvScore)
            }
            
            table.addView(row)
        }
    }
}
