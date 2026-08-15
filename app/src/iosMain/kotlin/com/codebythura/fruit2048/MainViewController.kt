package com.codebythura.fruit2048

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * TEMPORARY: Compose but with NO text (no font loading) — just a blue box.
 * Blue box shows -> Compose/Skia/Metal rendering works, and text/font rendering is the crash.
 * Still crashes -> Compose rendering itself (Skia/Metal init) is the problem.
 */
fun MainViewController(): UIViewController = ComposeUIViewController {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1565C0)),
    )
}
