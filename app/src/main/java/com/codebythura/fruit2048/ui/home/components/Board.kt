package com.codebythura.fruit2048.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.codebythura.fruit2048.data.GRID_SIZE
import com.codebythura.fruit2048.data.TileData
import com.codebythura.fruit2048.data.defaultCornerRadius
import com.codebythura.fruit2048.data.defaultDurationMillis
import com.codebythura.fruit2048.data.defaultPaddingDp
import com.codebythura.fruit2048.ui.theme.Fruit2048Theme


@Composable
fun Board(
    tileData: List<TileData> = emptyList(),
    gridSize: Int = GRID_SIZE,
    padding: Dp = defaultPaddingDp,
    cornerRadius: Dp = defaultCornerRadius,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    tileBgColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .boardBackground(
                gridSize = gridSize,
                paddingDp = padding,
                cornerRadius = cornerRadius,
                backgroundColor = backgroundColor,
                tileBgColor = tileBgColor,
            ),
    ) {
        val boardSizeDp = minOf(maxWidth, maxHeight)
        val tileSizeDp = (boardSizeDp - padding * (gridSize + 1)) / gridSize
        fun getOffset(index: Int): Dp = padding * (index + 1) + (tileSizeDp * index)
        for (tile in tileData) {
            key(tile.id) {
                val animateOffsetX by animateDpAsState(
                    targetValue = getOffset(tile.col),
                    animationSpec = tween(defaultDurationMillis, easing = FastOutSlowInEasing)
                )
                val animateOffsetY by animateDpAsState(
                    targetValue = getOffset(tile.row),
                    animationSpec = tween(defaultDurationMillis, easing = FastOutSlowInEasing)
                )
                TileItem(
                    tileData = tile,
                    modifier = Modifier
                        .offset(animateOffsetX, animateOffsetY)
                        .size(tileSizeDp)
                )
            }
        }
    }
}


private fun Modifier.boardBackground(
    gridSize: Int,
    paddingDp: Dp,
    cornerRadius: Dp,
    backgroundColor: Color,
    tileBgColor: Color,
) = this then aspectRatio(1f).drawBehind {
    val cornerRadiusPx = cornerRadius.toPx()
    val paddingPx = paddingDp.toPx()
    val boardSizePx = size.minDimension
    val tileSizePx = (boardSizePx - paddingPx * (gridSize + 1)) / gridSize
    drawRoundRect(
        color = backgroundColor,
        cornerRadius = CornerRadius(cornerRadiusPx)
    )
    for (i in 0 until gridSize) {
        val offsetY = paddingPx * (i + 1) + (tileSizePx * i)
        for (j in 0 until gridSize) {
            val offsetX = paddingPx * (j + 1) + (tileSizePx * j)
            drawRoundRect(
                color = tileBgColor,
                topLeft = Offset(offsetX, offsetY),
                cornerRadius = CornerRadius(cornerRadiusPx),
                size = Size(tileSizePx, tileSizePx)
            )
        }
    }
}


@Preview
@Composable
private fun BoardPreview() {
    Fruit2048Theme {
        Board(
            tileData = emptyList(),
            modifier = Modifier
        )
    }
}