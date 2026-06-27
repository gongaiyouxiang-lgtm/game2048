package com.codebythura.fruit2048.repository

import androidx.room.withTransaction
import com.codebythura.fruit2048.database.AppDatabase
import com.codebythura.fruit2048.database.GameStateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GameStateRepository {
    fun observeRowCount(): Flow<Int>
    suspend fun getAndDeleteLastState(): GameStateEntity?
    suspend fun saveState(state: GameStateEntity)
    suspend fun deleteAllStates()
}

class GameStateRepositoryImpl @Inject constructor(private val appDatabase: AppDatabase) : GameStateRepository {

    val gameStateDao = appDatabase.gameStateDao

    override fun observeRowCount() = gameStateDao.observeRowCount().flowOn(Dispatchers.IO)

    override suspend fun getAndDeleteLastState() = withContext(Dispatchers.IO) {
        appDatabase.withTransaction {
            val lastState = gameStateDao.getLatestState()
            lastState?.let { gameStateDao.deleteLatestState() }
            lastState
        }
    }

    override suspend fun saveState(state: GameStateEntity) = withContext(Dispatchers.IO) {
        gameStateDao.insertGameState(state)
    }

    override suspend fun deleteAllStates() = withContext(Dispatchers.IO) {
        gameStateDao.deleteAllGameStates()
    }

}