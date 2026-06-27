package com.codebythura.fruit2048.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codebythura.fruit2048.data.GridMatrix

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val state : GridMatrix,
    val score : Int,
)

