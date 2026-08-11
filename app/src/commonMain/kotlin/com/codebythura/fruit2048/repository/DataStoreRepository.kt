package com.codebythura.fruit2048.repository

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.data.GRID_SIZE
import com.codebythura.fruit2048.data.GridMatrix
import com.codebythura.fruit2048.database.GridConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** An in-progress game persisted so the player can resume it later. */
data class SavedGame(
    val board: GridMatrix,
    val score: Int,
    val gridSize: Int,
)

interface DataStoreRepository {
    fun observeBestScore(): Flow<Int>
    suspend fun updateBestScoreIfNecessary(newScore: Int) : Boolean
    fun observeGridSize(): Flow<Int>
    suspend fun setGridSize(size: Int)
    fun observeSoundEnabled(): Flow<Boolean>
    suspend fun setSoundEnabled(enabled: Boolean)
    fun observeVibrationEnabled(): Flow<Boolean>
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun saveCurrentGame(board: GridMatrix, score: Int, gridSize: Int)
    suspend fun loadCurrentGame(): SavedGame?
    suspend fun clearCurrentGame()
    fun observeHasActiveGame(): Flow<Boolean>
    fun observeSavedGridSize(): Flow<Int>
}

class DataStoreRepositoryImpl(private val datastore: DataStore<AppData>) :
    DataStoreRepository {

    override fun observeBestScore() = datastore.data.map { it.bestScore }.flowOn(Dispatchers.Default)

    override suspend fun updateBestScoreIfNecessary(newScore: Int): Boolean =
        withContext(Dispatchers.Default) {
            var updated = false
            datastore.updateData { current ->
                if (newScore > current.bestScore) {
                    updated = true
                    current.copy(bestScore = newScore)
                } else {
                    current
                }
            }
            return@withContext updated
        }

    // grid_size == 0 means "never set" -> fall back to the default (normal) board.
    override fun observeGridSize() = datastore.data
        .map { if (it.gridSize <= 0) GRID_SIZE else it.gridSize }
        .flowOn(Dispatchers.Default)

    override suspend fun setGridSize(size: Int) = withContext(Dispatchers.Default) {
        datastore.updateData { it.copy(gridSize = size) }
        Unit
    }

    // sound_muted defaults to false, so sound is enabled by default for new and existing data.
    override fun observeSoundEnabled() = datastore.data
        .map { !it.soundMuted }
        .flowOn(Dispatchers.Default)

    override suspend fun setSoundEnabled(enabled: Boolean) = withContext(Dispatchers.Default) {
        datastore.updateData { it.copy(soundMuted = !enabled) }
        Unit
    }

    // vibration_muted defaults to false, so vibration is enabled by default.
    override fun observeVibrationEnabled() = datastore.data
        .map { !it.vibrationMuted }
        .flowOn(Dispatchers.Default)

    override suspend fun setVibrationEnabled(enabled: Boolean) = withContext(Dispatchers.Default) {
        datastore.updateData { it.copy(vibrationMuted = !enabled) }
        Unit
    }

    private val gridConverter = GridConverter()

    override suspend fun saveCurrentGame(board: GridMatrix, score: Int, gridSize: Int) =
        withContext(Dispatchers.Default) {
            val serialized = gridConverter.fromBoard(board)
            datastore.updateData {
                it.copy(
                    currentBoard = serialized,
                    currentScore = score,
                    currentGridSize = gridSize,
                )
            }
            Unit
        }

    override suspend fun loadCurrentGame(): SavedGame? = withContext(Dispatchers.Default) {
        val data = datastore.data.first()
        if (data.currentBoard.isBlank()) return@withContext null
        SavedGame(
            board = gridConverter.toBoard(data.currentBoard),
            score = data.currentScore,
            gridSize = if (data.currentGridSize <= 0) GRID_SIZE else data.currentGridSize,
        )
    }

    override suspend fun clearCurrentGame() = withContext(Dispatchers.Default) {
        datastore.updateData { it.copy(currentBoard = "") }
        Unit
    }

    override fun observeHasActiveGame() = datastore.data
        .map { it.currentBoard.isNotBlank() }
        .flowOn(Dispatchers.Default)

    override fun observeSavedGridSize() = datastore.data
        .map { if (it.currentGridSize <= 0) GRID_SIZE else it.currentGridSize }
        .flowOn(Dispatchers.Default)

}