package com.codebythura.fruit2048.repository

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.data.GRID_SIZE
import com.codebythura.fruit2048.data.GridMatrix
import com.codebythura.fruit2048.database.GridConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    // A DataStore read/write error must never abort the app (e.g. iOS file-system hiccup):
    // reads fall back to defaults, writes are best-effort.
    private val data: Flow<AppData> = datastore.data.catch { emit(AppData()) }

    private suspend inline fun update(crossinline transform: (AppData) -> AppData) {
        runCatching { datastore.updateData { transform(it) } }
    }

    override fun observeBestScore() = data.map { it.bestScore }.flowOn(Dispatchers.Default)

    override suspend fun updateBestScoreIfNecessary(newScore: Int): Boolean =
        withContext(Dispatchers.Default) {
            var updated = false
            runCatching {
                datastore.updateData { current ->
                    if (newScore > current.bestScore) {
                        updated = true
                        current.copy(bestScore = newScore)
                    } else {
                        current
                    }
                }
            }
            updated
        }

    // grid_size == 0 means "never set" -> fall back to the default (normal) board.
    override fun observeGridSize() = data
        .map { if (it.gridSize <= 0) GRID_SIZE else it.gridSize }
        .flowOn(Dispatchers.Default)

    override suspend fun setGridSize(size: Int) = withContext(Dispatchers.Default) {
        update { it.copy(gridSize = size) }
    }

    // sound_muted defaults to false, so sound is enabled by default for new and existing data.
    override fun observeSoundEnabled() = data
        .map { !it.soundMuted }
        .flowOn(Dispatchers.Default)

    override suspend fun setSoundEnabled(enabled: Boolean) = withContext(Dispatchers.Default) {
        update { it.copy(soundMuted = !enabled) }
    }

    // vibration_muted defaults to false, so vibration is enabled by default.
    override fun observeVibrationEnabled() = data
        .map { !it.vibrationMuted }
        .flowOn(Dispatchers.Default)

    override suspend fun setVibrationEnabled(enabled: Boolean) = withContext(Dispatchers.Default) {
        update { it.copy(vibrationMuted = !enabled) }
    }

    private val gridConverter = GridConverter()

    override suspend fun saveCurrentGame(board: GridMatrix, score: Int, gridSize: Int) =
        withContext(Dispatchers.Default) {
            val serialized = gridConverter.fromBoard(board)
            update {
                it.copy(
                    currentBoard = serialized,
                    currentScore = score,
                    currentGridSize = gridSize,
                )
            }
        }

    override suspend fun loadCurrentGame(): SavedGame? = withContext(Dispatchers.Default) {
        val current = runCatching { datastore.data.first() }.getOrNull() ?: return@withContext null
        if (current.currentBoard.isBlank()) return@withContext null
        SavedGame(
            board = gridConverter.toBoard(current.currentBoard),
            score = current.currentScore,
            gridSize = if (current.currentGridSize <= 0) GRID_SIZE else current.currentGridSize,
        )
    }

    override suspend fun clearCurrentGame() = withContext(Dispatchers.Default) {
        update { it.copy(currentBoard = "") }
    }

    override fun observeHasActiveGame() = data
        .map { it.currentBoard.isNotBlank() }
        .flowOn(Dispatchers.Default)

    override fun observeSavedGridSize() = data
        .map { if (it.currentGridSize <= 0) GRID_SIZE else it.currentGridSize }
        .flowOn(Dispatchers.Default)

}
