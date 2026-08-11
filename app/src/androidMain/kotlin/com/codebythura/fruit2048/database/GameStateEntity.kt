package com.codebythura.fruit2048.database

import com.codebythura.fruit2048.data.GridMatrix

/** One undo-stack entry: a board plus its score. (Plain data class, no longer a Room entity.) */
data class GameStateEntity(
    val state: GridMatrix,
    val score: Int,
)
