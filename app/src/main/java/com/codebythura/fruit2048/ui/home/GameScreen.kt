package com.codebythura.fruit2048.ui.home

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codebythura.fruit2048.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codebythura.fruit2048.ads.BannerAd
import com.codebythura.fruit2048.ads.rememberInterstitialAdManager
import com.codebythura.fruit2048.data.Direction
import com.codebythura.fruit2048.data.TileData
import com.codebythura.fruit2048.data.defaultDurationMillis
import com.codebythura.fruit2048.ui.home.components.ActionsRow
import com.codebythura.fruit2048.util.findActivity
import com.codebythura.fruit2048.ui.home.components.AppTitle
import com.codebythura.fruit2048.ui.home.components.Board
import com.codebythura.fruit2048.ui.home.components.GameOverDialog
import com.codebythura.fruit2048.ui.home.components.NewBestScoreDialog
import com.codebythura.fruit2048.ui.home.components.ScoreRow
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun GameScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val interstitialAdManager = rememberInterstitialAdManager()
    LaunchedEffect(Unit) { interstitialAdManager.preload() }
    var showGameOverDialog by remember { mutableStateOf(false) }
    LaunchedEffect(state.isGameOver) {
        if (state.isGameOver) {
            delay(defaultDurationMillis.toLong()) // wait for the last tile animation
            context.findActivity()?.let { interstitialAdManager.maybeShowOnGameOver(it) }
            showGameOverDialog = true
        } else {
            showGameOverDialog = false
        }
    }
    if (showGameOverDialog) {
        GameOverDialog(
            score = state.score,
            onGameRestart = viewModel::startNewGame
        )
    }
    if (state.showNewScoreDialog) {
        NewBestScoreDialog(
            bestScore = state.bestScore,
            onDismiss = viewModel::onNewScoreDialogDismiss
        )
    }
    Scaffold(
        modifier = modifier,
        bottomBar = { BannerAd() },
        content = { innerPadding ->
            GameContent(
                bestScore = state.bestScore,
                score = state.score,
                tileData = state.tileData,
                gridSize = state.gridSize,
                onBack = onBack,
                onStartNewGame = viewModel::startNewGame,
                onUndo = viewModel::onUndo,
                undoEnabled = state.undoEnabled,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .onSwipe(onSwipe = viewModel::onSwipe)
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp)
            )
        }
    )
}

@Composable
fun GameContent(
    bestScore: Int,
    score: Int,
    tileData: List<TileData>,
    gridSize: Int,
    onBack: () -> Unit,
    onStartNewGame: () -> Unit,
    onUndo: () -> Unit,
    undoEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
            )
        }
        AppTitle()
        ScoreRow(bestScore = bestScore, score = score)
        ActionsRow(
            undoEnabled = undoEnabled,
            onUndo = onUndo,
            onStartNewGame = onStartNewGame
        )
        Board(tileData = tileData, gridSize = gridSize)
    }
}


private fun Modifier.onSwipe(
    threshold: Float = 50f,
    onSwipe: (Direction) -> Unit
): Modifier = this.pointerInput(Unit) {
    var dragAmountX = 0f
    var dragAmountY = 0f
    detectDragGestures(
        onDragStart = {
            dragAmountX = 0f
            dragAmountY = 0f
        },
        onDrag = { change, dragAmount ->
            change.consume()
            dragAmountX += dragAmount.x
            dragAmountY += dragAmount.y
        },
        onDragEnd = {
            val absX = abs(dragAmountX)
            val absY = abs(dragAmountY)

            if (absX > threshold || absY > threshold) {
                if (absX > absY) {
                    // Horizontal Swipe
                    onSwipe(if (dragAmountX > 0) Direction.RIGHT else Direction.LEFT)
                } else {
                    // Vertical Swipe
                    onSwipe(if (dragAmountY > 0) Direction.DOWN else Direction.UP)
                }
            }
        }
    )
}
