package com.codebythura.fruit2048.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.codebythura.fruit2048.util.rememberSoundManager
import kotlinx.coroutines.delay
import kotlin.math.abs

private val GameGradient = Brush.verticalGradient(
    listOf(Color(0xFF5EB1F7), Color(0xFF8C93F0), Color(0xFFB07CF0))
)
private val BoardPurple = Color(0xFF8B5CF6)
private val BoardCell = Color(0xFFA78BFA)

@Composable
fun GameScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sound = rememberSoundManager()
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(GameGradient),
        ) {
            // Floating fruits are drawn UNDER the card/board so the board stays on top (clean tiles)
            FloatingFruits(modifier = Modifier.fillMaxSize())

            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(Modifier.height(6.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White.copy(alpha = 0.55f),
                    shadowElevation = 8.dp,
                ) {
                    GameContent(
                        bestScore = state.bestScore,
                        score = state.score,
                        tileData = state.tileData,
                        gridSize = state.gridSize,
                        onBack = { sound.playClick(); onBack() },
                        onStartNewGame = { sound.playClick(); viewModel.startNewGame() },
                        onUndo = { sound.playClick(); viewModel.onUndo() },
                        undoEnabled = state.undoEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onSwipe(onSwipe = viewModel::onSwipe)
                            .padding(14.dp),
                    )
                }
            }
        }
    }
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(34.dp)
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
        Board(
            tileData = tileData,
            gridSize = gridSize,
            backgroundColor = BoardPurple,
            tileBgColor = BoardCell,
        )
    }
}

private data class FloatFruit(
    val emoji: String,
    val fx: Float,
    val fy: Float,
    val sizeSp: Int,
    val periodMs: Int,
    val amp: Float,
    val alpha: Float = 1f,
)

private val floatFruits = listOf(
    // Top corners
    FloatFruit("🍓", 0.010f, 0.040f, 40, 2600, 7f),
    FloatFruit("🍌", 0.855f, 0.035f, 42, 3000, 8f),
    // Left & right mid edges (flanking the board)
    FloatFruit("🍇", 0.000f, 0.28f, 42, 3200, 8f),
    FloatFruit("🍊", 0.895f, 0.25f, 36, 2700, 6f),
    FloatFruit("🍑", 0.010f, 0.56f, 40, 2900, 7f),
    FloatFruit("🍉", 0.870f, 0.55f, 42, 2500, 7f),
    // Bottom band — a row spread across the width, not just the corners
    FloatFruit("🍓", 0.030f, 0.865f, 36, 2750, 7f),
    FloatFruit("🍊", 0.250f, 0.910f, 34, 3050, 7f),
    FloatFruit("🍇", 0.460f, 0.860f, 40, 2650, 8f),
    FloatFruit("🍉", 0.665f, 0.905f, 36, 2850, 7f),
    FloatFruit("🍌", 0.850f, 0.865f, 40, 3100, 8f),
    // Small twinkles over the board (subtle, won't block tiles)
    FloatFruit("✨", 0.44f, 0.028f, 22, 2400, 5f, 0.9f),
    FloatFruit("⭐", 0.26f, 0.19f, 20, 2600, 5f, 0.8f),
    FloatFruit("✨", 0.70f, 0.18f, 22, 2800, 5f, 0.8f),
    FloatFruit("⭐", 0.16f, 0.42f, 20, 2500, 5f, 0.75f),
    FloatFruit("✨", 0.80f, 0.40f, 22, 2700, 5f, 0.75f),
)

@Composable
private fun FloatingFruits(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val w = maxWidth
        val h = maxHeight
        val transition = rememberInfiniteTransition(label = "floatingFruits")
        floatFruits.forEachIndexed { i, fruit ->
            val ty by transition.animateFloat(
                initialValue = -fruit.amp,
                targetValue = fruit.amp,
                animationSpec = infiniteRepeatable(
                    animation = tween(fruit.periodMs, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "floatY$i",
            )
            Text(
                text = fruit.emoji,
                fontSize = fruit.sizeSp.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = w * fruit.fx, y = h * fruit.fy + ty.dp)
                    .alpha(fruit.alpha),
            )
        }
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
