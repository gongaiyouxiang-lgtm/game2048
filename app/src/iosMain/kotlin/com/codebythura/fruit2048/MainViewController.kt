package com.codebythura.fruit2048

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * TEMPORARY minimal diagnostic: only plain Compose (no resources, no Koin, no DataStore).
 * If this green text shows on the device, Compose Multiplatform itself renders on this iOS
 * version; if it still crashes, the problem is the Compose framework / iOS compatibility.
 * The real Koin + Fruit2048 entry point is restored once startup is fixed.
 */
fun MainViewController(): UIViewController = ComposeUIViewController {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(40.dp),
    ) {
        BasicText(
            text = "STEP 1 OK: Compose renders on iOS",
            style = TextStyle(color = Color(0xFF2E7D32), fontSize = 20.sp, fontWeight = FontWeight.Bold),
        )
    }
}
