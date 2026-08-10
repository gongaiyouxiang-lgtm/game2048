package com.codebythura.fruit2048.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebythura.fruit2048.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUIState(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepository,
) : ViewModel() {

    val uiState = combine(
        dataStoreRepo.observeSoundEnabled(),
        dataStoreRepo.observeVibrationEnabled(),
    ) { sound, vibration ->
        SettingsUIState(soundEnabled = sound, vibrationEnabled = vibration)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUIState(),
    )

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { dataStoreRepo.setSoundEnabled(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { dataStoreRepo.setVibrationEnabled(enabled) }
    }
}
