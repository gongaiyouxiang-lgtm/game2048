package com.codebythura.fruit2048.repository

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.data.SavedBoard
import com.codebythura.fruit2048.database.GameStateEntity
import com.codebythura.fruit2048.database.GridConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface GameStateRepository {
    fun observeRowCount(): Flow<Int>
    suspend fun getAndDeleteLastState(): GameStateEntity?
    suspend fun saveState(state: GameStateEntity)
    suspend fun deleteAllStates()
}

/**
 * Undo stack backed by the DataStore [AppData.undoStack] (replaces the Room game_state table),
 * so it works in Kotlin Multiplatform common code.
 */
class GameStateRepositoryImpl(
    private val datastore: DataStore<AppData>,
) : GameStateRepository {

    private val gridConverter = GridConverter()

    override fun observeRowCount(): Flow<Int> =
        datastore.data.map { it.undoStack.size }.catch { emit(0) }.flowOn(Dispatchers.Default)

    override suspend fun saveState(state: GameStateEntity) = withContext(Dispatchers.Default) {
        val entry = SavedBoard(board = gridConverter.fromBoard(state.state), score = state.score)
        runCatching { datastore.updateData { it.copy(undoStack = it.undoStack + entry) } }
        Unit
    }

    override suspend fun getAndDeleteLastState(): GameStateEntity? = withContext(Dispatchers.Default) {
        var popped: SavedBoard? = null
        runCatching {
            datastore.updateData { current ->
                val stack = current.undoStack
                if (stack.isEmpty()) {
                    current
                } else {
                    popped = stack.last()
                    current.copy(undoStack = stack.dropLast(1))
                }
            }
        }
        popped?.let { GameStateEntity(state = gridConverter.toBoard(it.board), score = it.score) }
    }

    override suspend fun deleteAllStates() = withContext(Dispatchers.Default) {
        runCatching { datastore.updateData { it.copy(undoStack = emptyList()) } }
        Unit
    }
}
