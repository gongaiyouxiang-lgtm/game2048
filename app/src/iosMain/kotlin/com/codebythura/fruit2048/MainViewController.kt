package com.codebythura.fruit2048

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeUIViewController
import com.codebythura.fruit2048.di.initKoin
import com.codebythura.fruit2048.di.platformModule
import platform.UIKit.UIViewController

/**
 * TEMPORARY diagnostic entry point. The risky probing logic lives in the (Android-verified)
 * common [startupDiagnosticReport]; here we only start Koin and show the report.
 */
fun MainViewController(): UIViewController {
    val report = try {
        val app = initKoin(platformModule)
        startupDiagnosticReport(app.koin)
    } catch (t: Throwable) {
        "startup FAIL: $t"
    }
    return ComposeUIViewController {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF))
                .padding(40.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            BasicText(text = report, style = TextStyle(color = Color(0xFF000000), fontSize = 13.sp))
        }
    }
}
