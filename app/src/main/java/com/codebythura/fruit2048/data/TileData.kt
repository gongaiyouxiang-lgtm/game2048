package com.codebythura.fruit2048.data


data class TileData(
    val id: String,
    var value: Int,
    var row: Int,
    var col: Int,
    val shouldAnimated : Boolean,
)
