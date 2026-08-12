package com.example.okey101

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.okey101.data.AppDatabase
import com.example.okey101.data.Game
import com.example.okey101.data.Player
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var adapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            throwable.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "CRASH: ${throwable.localizedMessage}", Toast.LENGTH_LONG).show()
                AlertDialog.Builder(this)
                    .setTitle("CRITICAL ERROR")
                    .setMessage(throwable.stackTraceToString())
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        val rvGames = findViewById<RecyclerView>(R.id.rvGames)
        adapter = GameAdapter(
            onClick = { game ->
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("GAME_ID", game.gameId)
                startActivity(intent)
            },
            onDeleteClick = { game ->
                showDeleteGameDialog(game)
            }
        )
        rvGames.layoutManager = LinearLayoutManager(this)
        rvGames.adapter = adapter

        // Observe games
        lifecycleScope.launch {
            database.gameDao().getAllGames().collect { games ->
                adapter.submitList(games)
            }
        }

        findViewById<Button>(R.id.btnNewGame).setOnClickListener {
            showNewGameDialog()
        }

        findViewById<Button>(R.id.btnStats).setOnClickListener {
             startActivity(Intent(this, StatisticsActivity::class.java))
        }
        
        checkAndScheduleNotification()
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun checkAndScheduleNotification() {
        val alarmManager = getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        
        // Check if pending intent already exists (NO_CREATE flag)
        val existingIntent = android.app.PendingIntent.getBroadcast(
            this, 
            0, 
            intent, 
            android.app.PendingIntent.FLAG_NO_CREATE or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        if (existingIntent == null) {
            scheduleWeeklyNotification()
        }
    }

    private fun scheduleWeeklyNotification() {
        val alarmManager = getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            this, 
            0, 
            intent, 
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        
        // Set to Wednesday
        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.WEDNESDAY)
        // Set time to 19:00
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 19)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)

        // If today is Wednesday and it's already past 19:00, or if today is after Wednesday
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 7)
        }

        try {
            alarmManager.setRepeating(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                android.app.AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
            // Toast.makeText(this, "Haftalık hatırlatıcı kuruldu: Çarşamba 19:00", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNewGameDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_game, null)
        val etHost = dialogView.findViewById<EditText>(R.id.etHostName)
        val etP1 = dialogView.findViewById<EditText>(R.id.etPlayer1Name)
        val etP2 = dialogView.findViewById<EditText>(R.id.etPlayer2Name)
        val etP3 = dialogView.findViewById<EditText>(R.id.etPlayer3Name)
        val etP4 = dialogView.findViewById<EditText>(R.id.etPlayer4Name)

        val builder = AlertDialog.Builder(this)
        // Title is inside the custom view (dialog_new_game.xml)
        builder.setView(dialogView)
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            val hostName = etHost.text.toString()
            val p1Name = etP1.text.toString()
            val p2Name = etP2.text.toString()
            val p3Name = etP3.text.toString()
            val p4Name = etP4.text.toString()

            if (hostName.isBlank() || p1Name.isBlank() || p2Name.isBlank() || p3Name.isBlank() || p4Name.isBlank()) {
                Toast.makeText(this, getString(R.string.enter_all_names), Toast.LENGTH_SHORT).show()
            } else {
                createNewGame(hostName, listOf(p1Name, p2Name, p3Name, p4Name))
            }
        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.show()
    }

    private fun createNewGame(hostName: String, playerNames: List<String>) {
        lifecycleScope.launch {
            try {
                val game = Game(hostName = hostName)
                val gameId = database.gameDao().insertGame(game)
                
                playerNames.forEach { name ->
                    database.playerDao().insertPlayer(Player(gameId = gameId, name = name))
                }

                val intent = Intent(this@MainActivity, GameActivity::class.java)
                intent.putExtra("GAME_ID", gameId)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Hata")
                        .setMessage(e.localizedMessage ?: "Bilinmeyen bir hata oluştu")
                        .setPositiveButton("Tamam", null)
                        .show()
                }
            }
        }
    }

    private fun showDeleteGameDialog(game: Game) {
        AlertDialog.Builder(this)
            .setTitle("Oyunu Sil")
            .setMessage("Bu oyunu ve tüm puanlarını silmek istediğinize emin misiniz?")
            .setPositiveButton("Sil") { _, _ ->
                lifecycleScope.launch {
                    database.gameDao().deleteGame(game)
                    Toast.makeText(this@MainActivity, "Oyun silindi.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }
}
