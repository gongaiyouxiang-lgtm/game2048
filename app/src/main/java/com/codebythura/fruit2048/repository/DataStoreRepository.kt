package com.codebythura.fruit2048.repository

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.AppData
import com.codebythura.fruit2048.copy
import com.codebythura.fruit2048.data.GRID_SIZE
import com.codebythura.fruit2048.data.GridMatrix
import com.codebythura.fruit2048.database.GridConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
    suspend fun saveCurrentGame(board: GridMatrix, score: Int, gridSize: Int)
    suspend fun loadCurrentGame(): SavedGame?
    suspend fun clearCurrentGame()
    fun observeHasActiveGame(): Flow<Boolean>
    fun observeSavedGridSize(): Flow<Int>
}

class DataStoreRepositoryImpl @Inject constructor(private val datastore: DataStore<AppData>) :
    DataStoreRepository {

    override fun observeBestScore() = datastore.data.map { it.bestScore }.flowOn(Dispatchers.IO)

    override suspend fun updateBestScoreIfNecessary(newScore: Int): Boolean =
        withContext(Dispatchers.IO) {
            var updated = false
            datastore.updateData { currentData ->
                currentData.copy {
                    if (newScore > bestScore) {
                        bestScore = newScore
                        updated = true
                    }
                }
            }
            return@withContext updated
        }

    // grid_size == 0 means "never set" -> fall back to the default (normal) board.
    override fun observeGridSize() = datastore.data
        .map { if (it.gridSize <= 0) GRID_SIZE else it.gridSize }
        .flowOn(Dispatchers.IO)

    override suspend fun setGridSize(size: Int) = withContext(Dispatchers.IO) {
        datastore.updateData { it.copy { gridSize = size } }
        Unit
    }

    // sound_muted defaults to false, so sound is enabled by default for new and existing data.
    override fun observeSoundEnabled() = datastore.data
        .map { !it.soundMuted }
        .flowOn(Dispatchers.IO)

    override suspend fun setSoundEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        datastore.updateData { it.copy { soundMuted = !enabled } }
        Unit
    }

    private val gridConverter = GridConverter()

    override suspend fun saveCurrentGame(board: GridMatrix, score: Int, gridSize: Int) =
        withContext(Dispatchers.IO) {
            val serialized = gridConverter.fromBoard(board)
            datastore.updateData {
                it.copy {
                    currentBoard = serialized
                    currentScore = score
                    currentGridSize = gridSize
                }
            }
            Unit
        }

    override suspend fun loadCurrentGame(): SavedGame? = withContext(Dispatchers.IO) {
        val data = datastore.data.first()
        if (data.currentBoard.isBlank()) return@withContext null
        SavedGame(
            board = gridConverter.toBoard(data.currentBoard),
            score = data.currentScore,
            gridSize = if (data.currentGridSize <= 0) GRID_SIZE else data.currentGridSize,
        )
    }

    override suspend fun clearCurrentGame() = withContext(Dispatchers.IO) {
        datastore.updateData { it.copy { currentBoard = "" } }
        Unit
    }

    override fun observeHasActiveGame() = datastore.data
        .map { it.currentBoard.isNotBlank() }
        .flowOn(Dispatchers.IO)

    override fun observeSavedGridSize() = datastore.data
        .map { if (it.currentGridSize <= 0) GRID_SIZE else it.currentGridSize }
        .flowOn(Dispatchers.IO)

}