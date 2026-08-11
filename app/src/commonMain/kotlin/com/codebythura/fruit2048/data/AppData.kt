package com.codebythura.fruit2048.data

import kotlinx.serialization.Serializable

/**
 * Persisted app state (settings + in-progress game). Plain @Serializable data class so it
 * works in Kotlin Multiplatform common code (replaces the JVM-only generated protobuf type).
 */
@Serializable
data class AppData(
    val bestScore: Int = 0,
    val gridSize: Int = 0,
    val soundMuted: Boolean = false,
    val vibrationMuted: Boolean = false,
    val currentBoard: String = "",
    val currentScore: Int = 0,
    val currentGridSize: Int = 0,
    // Undo history (replaces the Room game_state table). Each entry is a serialized board.
    val undoStack: List<SavedBoard> = emptyList(),
)

@Serializable
data class SavedBoard(val board: String, val score: Int)
