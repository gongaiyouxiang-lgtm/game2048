package com.codebythura.fruit2048.di

import androidx.datastore.core.DataStore
import com.codebythura.fruit2048.ads.InterstitialAdManager
import com.codebythura.fruit2048.data.AppData
import com.codebythura.fruit2048.data.createAppDataStore
import com.codebythura.fruit2048.repository.DataStoreRepository
import com.codebythura.fruit2048.repository.DataStoreRepositoryImpl
import com.codebythura.fruit2048.repository.GameStateRepository
import com.codebythura.fruit2048.repository.GameStateRepositoryImpl
import com.codebythura.fruit2048.ui.home.GameViewModel
import com.codebythura.fruit2048.ui.landing.LandingViewModel
import com.codebythura.fruit2048.ui.settings.SettingsViewModel
import com.codebythura.fruit2048.util.SoundManager
import com.codebythura.fruit2048.util.VibrationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<DataStore<AppData>> {
        createAppDataStore {
            androidContext().filesDir.resolve("datastore/app_data.json").absolutePath
        }
    }
    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }
    single<GameStateRepository> { GameStateRepositoryImpl(get()) }
    single { SoundManager(androidContext(), get()) }
    single { VibrationManager(androidContext()) }
    single { InterstitialAdManager(androidContext()) }

    viewModel { GameViewModel(get(), get(), get(), get(), get()) }
    viewModel { LandingViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
