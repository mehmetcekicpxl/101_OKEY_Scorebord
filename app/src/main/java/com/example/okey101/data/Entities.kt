package com.example.okey101.data

import androidx.room.*
import java.util.Date

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val gameId: Long = 0,
    val date: Long = System.currentTimeMillis(), // Timestamp
    val hostName: String,
    val isFinished: Boolean = false
)

@Entity(tableName = "players",
    foreignKeys = [ForeignKey(entity = Game::class, parentColumns = ["gameId"], childColumns = ["gameId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["gameId"])]
)
data class Player(
    @PrimaryKey(autoGenerate = true) val playerId: Long = 0,
    val gameId: Long,
    val name: String,
    val totalScore: Int = 0
)

@Entity(tableName = "scores",
    foreignKeys = [ForeignKey(entity = Player::class, parentColumns = ["playerId"], childColumns = ["playerId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["playerId"])]
)
data class Score(
    @PrimaryKey(autoGenerate = true) val scoreId: Long = 0,
    val playerId: Long,
    val roundNumber: Int,
    val scoreValue: Int
)

// DAOs
@Dao
interface GameDao {
    @Insert
    suspend fun insertGame(game: Game): Long

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("SELECT * FROM games ORDER BY date DESC")
    fun getAllGames(): kotlinx.coroutines.flow.Flow<List<Game>>

    @Query("SELECT * FROM games")
    suspend fun getAllGamesSync(): List<Game>

    @Query("SELECT * FROM games WHERE gameId = :gameId")
    suspend fun getGame(gameId: Long): Game
}

@Dao
interface PlayerDao {
    @Insert
    suspend fun insertPlayer(player: Player): Long

    @Update
    suspend fun updatePlayer(player: Player)

    @Query("SELECT * FROM players WHERE gameId = :gameId")
    fun getPlayersForGame(gameId: Long): kotlinx.coroutines.flow.Flow<List<Player>>

    @Query("SELECT * FROM players WHERE gameId = :gameId")
    suspend fun getPlayersForGameSync(gameId: Long): List<Player>

    @Query("SELECT * FROM players")
    suspend fun getAllPlayers(): List<Player>
}

@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(score: Score): Long

    @Query("SELECT * FROM scores WHERE playerId = :playerId")
    suspend fun getScoresForPlayer(playerId: Long): List<Score>
    
    @Query("SELECT SUM(scoreValue) FROM scores WHERE playerId = :playerId")
    suspend fun calculateTotalScore(playerId: Long): Int?
}
