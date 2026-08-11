@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class)

package com.codebythura.fruit2048.data

import kotlin.uuid.Uuid


data class GridData(
    val id : String = Uuid.random().toString(),
    var value : Int,
    val shouldAnimated : Boolean = true,
) {
    fun intoTile(row: Int,col: Int) = TileData(
        id = id,
        value = value,
        row = row,
        col = col,
        shouldAnimated = shouldAnimated,
    )

    override fun toString(): String {
        return "$value"
    }
}