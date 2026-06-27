package com.codebythura.fruit2048.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebythura.fruit2048.data.GRID_SIZE
import com.codebythura.fruit2048.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LandingUIState(
    val gridSize: Int = GRID_SIZE,
    val soundEnabled: Boolean = true,
    val bestScore: Int = 0,
    val hasActiveGame: Boolean = false,
    val savedGridSize: Int = GRID_SIZE,
)

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepository,
) : ViewModel() {

    val uiState = combine(
        dataStoreRepo.observeGridSize(),
        dataStoreRepo.observeSoundEnabled(),
        dataStoreRepo.observeBestScore(),
        dataStoreRepo.observeHasActiveGame(),
        dataStoreRepo.observeSavedGridSize(),
    ) { gridSize, soundEnabled, bestScore, hasActiveGame, savedGridSize ->
        LandingUIState(
            gridSize = gridSize,
            soundEnabled = soundEnabled,
            bestScore = bestScore,
            hasActiveGame = hasActiveGame,
            savedGridSize = savedGridSize,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LandingUIState(),
    )

    fun setDifficulty(size: Int) {
        viewModelScope.launch { dataStoreRepo.setGridSize(size) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { dataStoreRepo.setSoundEnabled(enabled) }
    }
}
