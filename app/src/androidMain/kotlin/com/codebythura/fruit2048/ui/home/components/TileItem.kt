package com.codebythura.fruit2048.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.graphicsLayer
import com.codebythura.fruit2048.data.Tile
import com.codebythura.fruit2048.data.TileData
import com.codebythura.fruit2048.data.darkTileMap
import com.codebythura.fruit2048.data.defaultCornerRadius
import com.codebythura.fruit2048.data.defaultDurationMillis
import com.codebythura.fruit2048.data.lightTileMap
import kotlinx.coroutines.delay

@Composable
fun TileItem(
    tileData: TileData,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(if(tileData.shouldAnimated) 0f else 1f) }
    LaunchedEffect(tileData.id) {
        if(tileData.shouldAnimated) {
            delay(defaultDurationMillis.toLong())
            scale.snapTo(0.8f)
            scale.animateTo(
                targetValue = 1.12f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            )
        }
    }
    val tileMap = if(isSystemInDarkTheme()) darkTileMap else lightTileMap
    TileItem(
        tile = tileMap[tileData.value]!!,
        modifier = modifier.graphicsLayer {
            this.scaleX = scale.value
            this.scaleY = scale.value
        },
    )
}

@Composable
private fun TileItem(
    tile: Tile,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 12.dp
) {
    val animatedColor by animateColorAsState(targetValue = tile.color)
    val animatedBorderColor by animateColorAsState(targetValue = tile.borderColor)
    Surface(
        modifier = modifier.aspectRatio(1f),
        color = animatedColor,
        shape = RoundedCornerShape(defaultCornerRadius),
        border = BorderStroke(width = 2.dp, color = animatedBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Text(
                text = tile.value.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = tile.icon,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}