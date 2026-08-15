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
import com.codebythura.fruit2048.di.initKoin
import com.codebythura.fruit2048.di.platformModule
import com.codebythura.fruit2048.ui.Fruit2048
import com.codebythura.fruit2048.ui.theme.Fruit2048Theme
import platform.UIKit.UIViewController

// Start Koin exactly once, the first time the Compose view controller is created.
private val koinStarted: Unit by lazy {
    initKoin(platformModule)
    Unit
}

/** Entry point the Swift `iosApp` embeds: `ComposeApp.MainViewControllerKt.MainViewController()`. */
fun MainViewController(): UIViewController {
    installCrashReporter()
    // If the previous launch crashed, show the captured report instead of starting the app
    // (so the diagnostic screen renders even if Koin/app startup is what crashes).
    val crash = consumeCrashReport()
    if (crash != null) {
        return ComposeUIViewController { CrashReportScreen(crash) }
    }
    koinStarted
    return ComposeUIViewController {
        Fruit2048Theme {
            Fruit2048()
        }
    }
}

/** Minimal, dependency-free screen (no resources/theme/DI) that shows a captured crash. */
@Composable
private fun CrashReportScreen(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        BasicText(
            text = "Startup crash captured:",
            style = TextStyle(color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 15.sp),
        )
        Spacer(Modifier.height(12.dp))
        BasicText(
            text = text,
            style = TextStyle(color = Color(0xFF111111), fontSize = 11.sp),
        )
    }
}
