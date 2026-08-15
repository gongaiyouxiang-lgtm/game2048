package com.codebythura.fruit2048

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * TEMPORARY bare-Compose test: no Koin, DataStore, resources, or runBlocking — just Compose.
 * If this green text shows, Compose Multiplatform 1.11.1 renders on iOS 26 and the crash is
 * app-level; if it still crashes, the framework/hosting itself is the problem.
 */
fun MainViewController(): UIViewController = ComposeUIViewController {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(40.dp),
    ) {
        BasicText(
            text = "Compose 1.11.1 renders on iOS 26",
            style = TextStyle(color = Color(0xFF0A7D2C), fontSize = 20.sp),
        )
    }
}
