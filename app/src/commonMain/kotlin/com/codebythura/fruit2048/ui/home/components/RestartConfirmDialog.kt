package com.codebythura.fruit2048.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.codebythura.fruit2048.resources.*


@Composable
fun RestartConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.confirm),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = stringResource(Res.string.restart_confirm_message),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 12.dp, bottom = 18.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RestartButton(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )
                    CancelButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


@Composable
private fun RestartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
    ) {
        Text(stringResource(Res.string.restart))
    }
}

@Composable
private fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
    ) {
        Text(text = stringResource(Res.string.cancel))
    }
}