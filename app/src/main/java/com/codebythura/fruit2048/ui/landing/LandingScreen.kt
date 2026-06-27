package com.codebythura.fruit2048.ui.landing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codebythura.fruit2048.R
import com.codebythura.fruit2048.ads.BannerAd
import com.codebythura.fruit2048.ui.home.components.AppTitle
import com.codebythura.fruit2048.util.LocaleManager
import com.codebythura.fruit2048.util.findActivity

private data class DifficultyOption(val size: Int, val labelRes: Int)

private val difficultyOptions = listOf(
    DifficultyOption(size = 5, labelRes = R.string.difficulty_easy),
    DifficultyOption(size = 4, labelRes = R.string.difficulty_normal),
    DifficultyOption(size = 3, labelRes = R.string.difficulty_hard),
)

private data class LanguageOption(val tag: String, val labelRes: Int)

private val languageOptions = listOf(
    LanguageOption(tag = "en", labelRes = R.string.lang_english),
    LanguageOption(tag = "zh-TW", labelRes = R.string.lang_traditional_chinese),
    LanguageOption(tag = "zh-CN", labelRes = R.string.lang_simplified_chinese),
)

@Composable
fun LandingScreen(
    onStartGame: (gridSize: Int) -> Unit,
    onContinueGame: (gridSize: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(LocaleManager.getLanguage(context)) }

    Scaffold(
        modifier = modifier,
        bottomBar = { BannerAd() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AppTitle()

            BestScoreBanner(bestScore = state.bestScore)

            SettingSection(title = stringResource(R.string.difficulty)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    difficultyOptions.forEach { option ->
                        OptionCard(
                            selected = state.gridSize == option.size,
                            onClick = { viewModel.setDifficulty(option.size) },
                        ) {
                            Text(
                                text = stringResource(option.labelRes),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            )
                            Text(
                                text = "${option.size}×${option.size}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }

            SettingSection(title = stringResource(R.string.sound)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.sound),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Switch(
                            checked = state.soundEnabled,
                            onCheckedChange = { viewModel.setSoundEnabled(it) },
                        )
                    }
                }
            }

            SettingSection(title = stringResource(R.string.language)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    languageOptions.forEach { option ->
                        OptionCard(
                            selected = currentLanguage == option.tag,
                            onClick = {
                                if (currentLanguage != option.tag) {
                                    LocaleManager.setLanguage(context, option.tag)
                                    currentLanguage = option.tag
                                    context.findActivity()?.recreate()
                                }
                            },
                        ) {
                            Text(
                                text = stringResource(option.labelRes),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.hasActiveGame) {
                Button(
                    onClick = { onContinueGame(state.savedGridSize) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text(
                        text = stringResource(R.string.continue_game),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }

            val startButtonColors = if (state.hasActiveGame) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            } else {
                ButtonDefaults.buttonColors()
            }
            Button(
                onClick = { onStartGame(state.gridSize) },
                shape = RoundedCornerShape(12.dp),
                colors = startButtonColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(
                    text = stringResource(R.string.start_game),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

@Composable
private fun BestScoreBanner(bestScore: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.best_score),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = bestScore.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        content()
    }
}

@Composable
private fun RowScope.OptionCard(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            content = content,
        )
    }
}
