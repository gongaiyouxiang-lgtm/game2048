package com.codebythura.fruit2048.data

import kotlin.random.Random


const val GRID_SIZE = 4
typealias GridMatrix = List<List<GridData?>>
val EMPTY_GRID: GridMatrix = emptyGrid(GRID_SIZE)

fun emptyGrid(size: Int = GRID_SIZE): GridMatrix = List(size) { List(size) { null } }

object GameLogic {

    fun checkIfGameOver(grid: GridMatrix): Boolean {
        val size = grid.size
        for (r in 0 until size) {
            for (c in 0 until size) {
                val currentValue = grid[r][c] ?: return false
                if(c + 1 < size) {
                    val rightValue = grid[r][c + 1]
                    if (rightValue == null || rightValue.value == currentValue.value) return false
                }
                if(r + 1 < size) {
                    val downValue = grid[r + 1][c]
                    if (downValue == null || downValue.value == currentValue.value) return false
                }
            }
        }
        return true
    }

    fun addRandomData(gridData: GridMatrix,count: Int = 1) : GridMatrix {
        if (count <= 0) throw IllegalArgumentException("Count must be greater than 0")
        val emptyCells = gridData.flatMapIndexed { row, list ->
            list.mapIndexedNotNull { col, data ->
                if (data == null) row to col else null
            }
        }.shuffled()
        if (emptyCells.isEmpty()) return gridData

        val actualCount = minOf(emptyCells.size,count)
        val newGrid = gridData.map { it.toMutableList() }.toMutableList()

        for (i in 0 until actualCount) {
            val cell = emptyCells[i]
            val value = if (Random.nextFloat() < 0.9f) 2 else 4
            newGrid[cell.first][cell.second] = GridData(value = value)
        }
        return newGrid
    }

    fun swipeDown(gridData: GridMatrix): Pair<GridMatrix,Int> {
        val rotated90Clockwise = rotate90Clockwise(gridData)
        val (swipedToLeft,score) = swipeToLeft(rotated90Clockwise)
        val rotated90AntiClockwise = rotate90AntiClockwise(swipedToLeft)
        return Pair(rotated90AntiClockwise,score)
    }

    fun swipeUp(gridData: GridMatrix): Pair<GridMatrix,Int> {
        val rotated90AntiClockwise = rotate90AntiClockwise(gridData)
        val (swipedToLeft,score) = swipeToLeft(rotated90AntiClockwise)
        val rotated90Clockwise = rotate90Clockwise(swipedToLeft)
        return Pair(rotated90Clockwise,score)
    }

    fun swipeToRight(gridData: GridMatrix): Pair<GridMatrix,Int> {
        val reversed = gridData.map { row -> row.reversed() }
        val (swipedToLeft,score) = swipeToLeft(reversed)
        val swipedToRight = swipedToLeft.map { row -> row.reversed() }
        return Pair(swipedToRight,score)
    }

    fun swipeToLeft(gridData: GridMatrix): Pair<GridMatrix,Int> {
        var totalScore = 0
        val swipedToLeft = gridData.map { row ->
            val (merged,score) = mergeToLeft(row)
            totalScore += score
            padToGridSize(merged, row.size)
        }
        return Pair(swipedToLeft,totalScore)
    }

    private fun rotate90AntiClockwise(gridData: GridMatrix): GridMatrix {
        val size = gridData.size
        return List(size) { row ->
            List(size) { col ->
                gridData[col][size - 1 - row]
            }
        }
    }

    private fun rotate90Clockwise(gridData: GridMatrix): GridMatrix {
        val size = gridData.size
        return List(size) { row ->
            List(size) { col ->
                gridData[size - 1 - col][row]
            }
        }
    }

    private fun padToGridSize(array: List<GridData?>, size: Int): List<GridData?> {
        val missing = (size - array.size).coerceAtLeast(0)
        return array + List(missing) { null }
    }

    private fun mergeToLeft(array: List<GridData?>): Pair<List<GridData?>,Int> {
        val notNulls = array.filterNotNull()
        val merged = mutableListOf<GridData>()
        var mergedScore = 0
        var i = 0
        while (i < notNulls.size) {
            if (i + 1 < notNulls.size && notNulls[i].value == notNulls[i + 1].value) {
                val data = notNulls[i]
                merged.add(data.copy(value = data.value * 2))
                i += 2
                mergedScore += data.value * 2
            } else {
                merged.add(notNulls[i])
                i++
            }
        }
        return Pair(merged,mergedScore)
    }

}