package com.codebythura.fruit2048.database

import androidx.room.TypeConverter
import com.codebythura.fruit2048.data.GRID_SIZE
import com.codebythura.fruit2048.data.GridData
import com.codebythura.fruit2048.data.GridMatrix

class GridConverter {

    @TypeConverter
    fun fromBoard(grid: GridMatrix): String {
        val size = grid.size
        val result = mutableListOf<String>()
        for (row in 0 until size) {
            for (col in 0 until size) {
                val value: GridData = grid[row][col] ?: continue
                result.add("($value,$row,$col)")
            }
        }
        // Prefix with the board size so undo restores the correct dimensions.
        return "$size;" + result.joinToString(":")
    }

    @TypeConverter
    fun toBoard(json: String): GridMatrix {
        val trimmed = json.trim()
        val separatorIndex = trimmed.indexOf(';')
        val size = trimmed.substringBefore(';').toIntOrNull() ?: GRID_SIZE
        val body = if (separatorIndex >= 0) trimmed.substring(separatorIndex + 1) else trimmed
        val grid: List<MutableList<GridData?>> = MutableList(size) { MutableList(size) { null } }
        if (body.isBlank()) return grid
        for (data in body.split(":")) {
            val (value, row, col) = data.removePrefix("(").removeSuffix(")").split(",")
            grid[row.toInt()][col.toInt()] = GridData(value = value.toInt(), shouldAnimated = false)
        }
        return grid
    }

}