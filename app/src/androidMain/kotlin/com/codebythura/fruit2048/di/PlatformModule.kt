package com.codebythura.fruit2048.di

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.ads.InterstitialAdManager
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.data.createAppDataStore
import com.codebythura.fruit2048.util.SoundManager
import com.codebythura.fruit2048.util.VibrationManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Android platform bindings (Context-backed services + the app DataStore). */
val platformModule = module {
    single<DataStore<AppData>> {
        createAppDataStore {
            androidContext().filesDir.resolve("datastore/app_data.json").absolutePath
        }
    }
    single { SoundManager(androidContext(), get()) }
    single { VibrationManager(androidContext()) }
    single { InterstitialAdManager(androidContext()) }
}
