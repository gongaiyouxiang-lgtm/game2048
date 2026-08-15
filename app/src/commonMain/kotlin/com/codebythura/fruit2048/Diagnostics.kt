package com.codebythura.fruit2048

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.resources.Res
import com.codebythura.fruit2048.resources.start_game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.koin.core.context.GlobalContext

/**
 * TEMPORARY iOS-bring-up probe. Runs the iOS-risky startup steps (DataStore read, resource
 * load) with per-step try/catch and returns an on-screen report. Lives in commonMain on
 * purpose so the Android build verifies it compiles — common code that compiles for Android
 * also compiles for the iOS target, avoiding blind iosMain compile failures.
 */
fun startupDiagnosticReport(): String {
    val sb = StringBuilder("Diagnostic (CMP 1.11.1)\n\n")

    fun step(name: String, block: suspend () -> Unit) {
        val line = try {
            runBlocking { block() }
            "OK    $name"
        } catch (t: Throwable) {
            "FAIL  $name\n        $t"
        }
        sb.append(line).append("\n\n")
    }

    step("1 Koin -> DataStore read") {
        val ds = GlobalContext.get().get<DataStore<AppData>>()
        ds.data.first()
    }
    step("2 Resource getString") {
        getString(Res.string.start_game)
    }

    return sb.toString()
}
