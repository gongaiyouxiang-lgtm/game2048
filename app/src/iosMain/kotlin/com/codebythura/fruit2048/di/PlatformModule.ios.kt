@file:OptIn(ExperimentalForeignApi::class)

package com.codebythura.fruit2048.di

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.ads.InterstitialAdManager
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.data.createAppDataStore
import com.codebythura.fruit2048.util.LocaleController
import com.codebythura.fruit2048.util.SoundManager
import com.codebythura.fruit2048.util.VibrationManager
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

/** iOS platform bindings (the app DataStore lives in the app's Documents directory). */
val platformModule = module {
    single<DataStore<AppData>> {
        createAppDataStore { iosDataStorePath() }
    }
    single { SoundManager(get()) }
    single { VibrationManager() }
    single { InterstitialAdManager() }
    single { LocaleController() }
}

private fun iosDataStorePath(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return (documentDirectory?.path ?: "") + "/app_data.json"
}
