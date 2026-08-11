package com.codebythura.fruit2048.ui.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codebythura.fruit2048.R
import com.codebythura.fruit2048.ads.BannerAd
import com.codebythura.fruit2048.util.SoundManager
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private data class DifficultyOption(val size: Int, val labelRes: Int)

private val difficultyOptions = listOf(
    DifficultyOption(size = 5, labelRes = R.string.difficulty_easy),
    DifficultyOption(size = 4, labelRes = R.string.difficulty_normal),
    DifficultyOption(size = 3, labelRes = R.string.difficulty_hard),
)

private val Primary = Color(0xFF176FE7)
private val BlueTop = Color(0xFF5AA0FF)
private val BlueBottom = Color(0xFF1466DA)
private val TextDark = Color(0xFF1C1B1F)
private val TextMuted = Color(0xFF6B6B70)
private val Sheen = Brush.verticalGradient(listOf(Color(0x66FFFFFF), Color(0x00FFFFFF)))
private val BlueFill = Brush.verticalGradient(listOf(BlueTop, BlueBottom))

/**
 * Image-backed landing: the AI-generated [R.drawable.home_bg_art] provides the hero art in
 * the top half; glossy, elevated Compose controls sit just below it in the lower area.
 * Sound/vibration/language live in the Settings screen.
 */
@Composable
fun LandingScreen(
    onStartGame: (gridSize: Int) -> Unit,
    onContinueGame: (gridSize: Int) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sound = koinInject<SoundManager>()

    Scaffold(
        modifier = modifier,
        bottomBar = { BannerAd() },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.home_bg_art),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                // Top overlay: best-score pill + settings gear
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.weight(1f))
                    BestScorePill(state.bestScore)
                    Spacer(Modifier.size(8.dp))
                    GearButton(onClick = { sound.playClick(); onOpenSettings() })
                }

                // Sit the controls just below the hero (lifted off the very bottom)
                Spacer(Modifier.weight(1f))

                SectionLabel(stringResource(R.string.difficulty))
                DifficultySegmented(
                    selected = state.gridSize,
                    onSelect = { sound.playClick(); viewModel.setDifficulty(it) },
                )

                Spacer(Modifier.height(22.dp))
                PlayButton { sound.playClick(); onStartGame(state.gridSize) }
                if (state.hasActiveGame) {
                    Spacer(Modifier.height(12.dp))
                    ContinueButton { sound.playClick(); onContinueGame(state.savedGridSize) }
                }

                // Extra breathing room below so the block floats above the bottom
                Spacer(Modifier.weight(0.3f))
            }
        }
    }
}

@Composable
private fun BestScorePill(bestScore: Int) {
    Row(
        modifier = Modifier
            .shadow(6.dp, RoundedCornerShape(50), clip = false)
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 15.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("🏆", fontSize = 15.sp)
        Text("  $bestScore", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun GearButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .shadow(6.dp, RoundedCornerShape(50), clip = false)
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text("⚙️", fontSize = 19.sp)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Primary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 6.dp),
    )
}

@Composable
private fun DifficultySegmented(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        difficultyOptions.forEach { option ->
            val isSel = selected == option.size
            val cellShape = RoundedCornerShape(15.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isSel) Modifier.shadow(8.dp, cellShape, clip = false, spotColor = Primary)
                        else Modifier
                    )
                    .clip(cellShape)
                    .then(if (isSel) Modifier.background(BlueFill) else Modifier)
                    .clickable { onSelect(option.size) }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(option.labelRes),
                    color = if (isSel) Color.White else TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
                Text(
                    text = "${option.size}×${option.size}",
                    color = if (isSel) Color(0xE6FFFFFF) else TextMuted,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun PlayButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(14.dp, shape, clip = false, spotColor = Primary, ambientColor = Primary)
            .clip(shape)
            .background(BlueFill)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // Glossy top highlight
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(50))
                .background(Sheen),
        )
        Text(
            text = "▶  " + stringResource(R.string.start_game),
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 19.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ContinueButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(6.dp, shape, clip = false)
            .clip(shape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.continue_game),
            color = Primary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}
