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
import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.di.initKoin
import com.codebythura.fruit2048.di.platformModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import platform.UIKit.UIViewController

/**
 * TEMPORARY diagnostic: run the three iOS-risky startup steps synchronously, each guarded,
 * and print OK / FAIL(+exception) on screen. Pinpoints the launch crash in one build.
 */
fun MainViewController(): UIViewController {
    val log = StringBuilder("Diagnostic (CMP 1.11.1)\n\n")

    fun step(name: String, block: () -> Unit) {
        val line = try {
            block()
            "OK    $name"
        } catch (t: Throwable) {
            "FAIL  $name\n        $t"
        }
        log.append(line).append("\n\n")
    }

    step("1 initKoin") {
        initKoin(platformModule)
    }
    step("2 DataStore read") {
        val ds = GlobalContext.get().get<DataStore<AppData>>()
        runBlocking { ds.data.first() }
    }

    val report = log.toString()
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
