package com.codebythura.fruit2048.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface GameStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(gameState: GameStateEntity)

    @Query("SELECT * FROM game_state ORDER BY id DESC LIMIT 1")
    fun getLatestState(): GameStateEntity?

    @Query("DELETE FROM game_state WHERE id = (SELECT MAX(id) FROM game_state)")
    suspend fun deleteLatestState()

    @Query("SELECT COUNT(*) FROM game_state")
    fun observeRowCount(): Flow<Int>

    @Query("DELETE FROM game_state")
    suspend fun deleteAllGameStates()

}