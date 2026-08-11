package com.codebythura.fruit2048.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
fun NewBestScoreDialog(
    bestScore: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.congratulation),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = stringResource(Res.string.new_best_score, bestScore),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(top = 12.dp, bottom = 18.dp)
                )
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.close))
                }
            }
        }
    }

}