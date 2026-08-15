package com.codebythura.fruit2048

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.resources.Res
import com.codebythura.fruit2048.resources.start_game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.koin.core.Koin

/**
 * TEMPORARY iOS-bring-up probe. Runs the iOS-risky startup steps (DataStore read, resource
 * load) with per-step try/catch and returns an on-screen report. In commonMain so the Android
 * build verifies it compiles; takes the [koin] instance explicitly (GlobalContext isn't on
 * the Native Koin API).
 */
fun startupDiagnosticReport(koin: Koin): String {
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

    step("1 DataStore read") {
        val ds = koin.get<DataStore<AppData>>()
        ds.data.first()
    }
    step("2 Resource getString") {
        getString(Res.string.start_game)
    }

    return sb.toString()
}
