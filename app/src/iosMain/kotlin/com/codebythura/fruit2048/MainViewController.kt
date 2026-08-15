package com.codebythura.fruit2048

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeUIViewController
import com.codebythura.fruit2048.resources.Res
import com.codebythura.fruit2048.resources.start_game
import org.jetbrains.compose.resources.stringResource
import platform.UIKit.UIViewController

/**
 * TEMPORARY iOS diagnostic entry point. Instead of launching the game it renders a staged
 * screen so we can see exactly where iOS startup breaks:
 *   - "STEP 1 OK" visible  -> Compose itself renders on iOS
 *   - "resource = [..]" filled -> Compose Multiplatform resources load on iOS
 *   - crashes after STEP 1  -> resource loading is the culprit
 *   - nothing shows / instant crash -> Compose framework issue
 * The real entry point (Koin + Fruit2048) is restored once the cause is fixed.
 */
fun MainViewController(): UIViewController {
    installCrashReporter()
    val crash = consumeCrashReport()
    return ComposeUIViewController {
        DiagnosticScreen(crash)
    }
}

@Composable
private fun DiagnosticScreen(crash: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        BasicText(
            text = "STEP 1 OK: Compose renders on iOS",
            style = TextStyle(color = Color(0xFF2E7D32), fontSize = 18.sp, fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(16.dp))
        BasicText(
            text = "STEP 2: load a resource string ->",
            style = TextStyle(color = Color(0xFF555555), fontSize = 13.sp),
        )
        BasicText(
            text = "resource = [" + stringResource(Res.string.start_game) + "]",
            style = TextStyle(color = Color(0xFF1565C0), fontSize = 16.sp, fontWeight = FontWeight.Bold),
        )
        if (crash != null) {
            Spacer(Modifier.height(20.dp))
            BasicText(
                text = "Captured crash:",
                style = TextStyle(color = Color(0xFFD32F2F), fontSize = 14.sp, fontWeight = FontWeight.Bold),
            )
            BasicText(text = crash, style = TextStyle(color = Color(0xFF111111), fontSize = 11.sp))
        }
    }
}
