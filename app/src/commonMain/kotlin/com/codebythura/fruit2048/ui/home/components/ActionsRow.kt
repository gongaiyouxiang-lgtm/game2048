package com.codebythura.fruit2048.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.codebythura.fruit2048.resources.*

private val buttonHeight = 50.dp
private val OrangeBtn = Color(0xFFFF9F43)
private val YellowBtn = Color(0xFFFFC93C)
private val YellowContent = Color(0xFF6B4E00)

@Composable
fun ActionsRow(
    undoEnabled: Boolean = true,
    onUndo: () -> Unit,
    startNewGameEnabled: Boolean = true,
    onStartNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
    ) {
        NewGameButton(
            enabled = startNewGameEnabled,
            onClick = onStartNewGame,
            modifier = Modifier.weight(1f)
        )
        UndoGameButton(
            enabled = undoEnabled,
            onClick = onUndo,
            modifier = Modifier
        )
    }
}

@Composable
fun UndoGameButton(
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ActionButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.size(buttonHeight),
        contentPadding = PaddingValues(0.dp),
        containerColor = YellowBtn,
        contentColor = YellowContent,
    ) {
        Icon(
            painter = painterResource(Res.drawable.undo),
            contentDescription = stringResource(Res.string.undo),
        )
    }
}

@Composable
fun NewGameButton(
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ActionButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.height(48.dp),
        containerColor = OrangeBtn,
        contentColor = Color.White,
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(Res.drawable.restart),
                contentDescription = stringResource(Res.string.new_game),
            )
            Text(
                text = stringResource(Res.string.new_game),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier,
        contentPadding = contentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.6f),
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        ),
        content = content,
    )
}