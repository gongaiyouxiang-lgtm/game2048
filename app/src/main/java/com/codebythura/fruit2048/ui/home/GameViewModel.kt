package com.codebythura.fruit2048.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebythura.fruit2048.repository.GameStateRepository
import com.codebythura.fruit2048.repository.DataStoreRepository
import com.codebythura.fruit2048.data.Direction
import com.codebythura.fruit2048.data.GRID_SIZE
import com.codebythura.fruit2048.data.GameLogic
import com.codebythura.fruit2048.data.GridMatrix
import com.codebythura.fruit2048.data.TileData
import com.codebythura.fruit2048.data.emptyGrid
import com.codebythura.fruit2048.database.GameStateEntity
import com.codebythura.fruit2048.util.SoundManager
import com.codebythura.fruit2048.util.VibrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUIState(
    val tileData: List<TileData> = emptyList(),
    val gridSize: Int = GRID_SIZE,
    val bestScore: Int = 0,
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val undoEnabled: Boolean = false,
    val showNewScoreDialog : Boolean = false,
)

private data class GameState(
    val gridData: GridMatrix,
    val score: Int = 0,
    val showNewScoreDialog: Boolean = false,
) {
    fun toTileData(): List<TileData> {
        return gridData.flatMapIndexed { row, list ->
            list.mapIndexedNotNull { col, data -> data?.intoTile(row, col) }
        }
    }
}

private fun GameStateEntity.toState() = GameState(
    gridData = state,
    score = score,
)

private fun GameState.toEntity() = GameStateEntity(
    state = gridData,
    score = score,
)

const val GRID_SIZE_ARG = "gridSize"
const val RESUME_ARG = "resume"

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameStateRepo: GameStateRepository,
    private val dataStoreRepo: DataStoreRepository,
    private val soundManager: SoundManager,
    private val vibrationManager: VibrationManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val gridSize: Int = savedStateHandle.get<Int>(GRID_SIZE_ARG)?.takeIf { it > 0 } ?: GRID_SIZE
    private val resume: Boolean = savedStateHandle.get<Boolean>(RESUME_ARG) ?: false

    private var vibrationEnabled = true

    private val _gameState = MutableStateFlow(GameState(gridData = emptyGrid(gridSize)))
    val uiState = combine(
        _gameState,
        gameStateRepo.observeRowCount(),
        dataStoreRepo.observeBestScore()
    ) { gameState, lastState, bestScore ->
        GameUIState(
            tileData = gameState.toTileData(),
            gridSize = gridSize,
            score = gameState.score,
            bestScore = bestScore,
            isGameOver = GameLogic.checkIfGameOver(gameState.gridData),
            undoEnabled = lastState > 0,
            showNewScoreDialog = gameState.showNewScoreDialog,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GameUIState(gridSize = gridSize)
    )

    init {
        dataStoreRepo.observeVibrationEnabled()
            .onEach { vibrationEnabled = it }
            .launchIn(viewModelScope)
        if (resume) {
            viewModelScope.launch {
                val saved = dataStoreRepo.loadCurrentGame()
                if (saved != null) {
                    // Restore the board without wiping the undo stack, so undo still works.
                    _gameState.value = GameState(gridData = saved.board, score = saved.score)
                } else {
                    startNewGame()
                }
            }
        } else {
            startNewGame()
        }
    }

    fun startNewGame() {
        viewModelScope.launch {
            gameStateRepo.deleteAllStates() // delete all game state from db
            val showNewScoreDialog = dataStoreRepo.updateBestScoreIfNecessary(_gameState.value.score)
            val initialGrid = GameLogic.addRandomData(emptyGrid(gridSize), count = 2)
            val newState = GameState(
                gridData = initialGrid,
                score = 0,
                showNewScoreDialog = showNewScoreDialog,
            )
            _gameState.value = newState
            if (showNewScoreDialog) soundManager.playWin()
            persist(newState)
        }
    }

    fun onNewScoreDialogDismiss() {
        _gameState.update { it.copy(showNewScoreDialog = false) }
    }

    fun onUndo() {
        viewModelScope.launch {
            gameStateRepo.getAndDeleteLastState()?.let { stateFromDb ->
                val restored = stateFromDb.toState()
                _gameState.value = restored
                persist(restored)
            }
        }
    }

    fun onSwipe(direction: Direction) {
        viewModelScope.launch {
            gameStateRepo.saveState(_gameState.value.toEntity()) // save the current state first
            val (swipedGrid, newScore) = when (direction) {
                Direction.LEFT -> GameLogic.swipeToLeft(_gameState.value.gridData)
                Direction.RIGHT -> GameLogic.swipeToRight(_gameState.value.gridData)
                Direction.UP -> GameLogic.swipeUp(_gameState.value.gridData)
                Direction.DOWN -> GameLogic.swipeDown(_gameState.value.gridData)
            }
            val moved = swipedGrid != _gameState.value.gridData
            if (newScore > 0) {
                soundManager.playMerge()
                if (vibrationEnabled) vibrationManager.vibrateMerge()
            } else if (moved) {
                soundManager.playSlide()
            }
            val newGrid = GameLogic.addRandomData(swipedGrid)
            val newState = GameState(
                gridData = newGrid,
                score = _gameState.value.score + newScore
            )
            _gameState.value = newState
            if (GameLogic.checkIfGameOver(newGrid)) {
                soundManager.playGameOver()
            }
            persist(newState)
        }
    }

    /** Persist the live board so it can be resumed; clear it once the game is over. */
    private suspend fun persist(state: GameState) {
        if (GameLogic.checkIfGameOver(state.gridData)) {
            dataStoreRepo.clearCurrentGame()
        } else {
            dataStoreRepo.saveCurrentGame(state.gridData, state.score, gridSize)
        }
    }

}
