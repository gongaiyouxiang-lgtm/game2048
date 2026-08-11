package com.codebythura.fruit2048.di

import com.codebythura.fruit2048.repository.DataStoreRepository
import com.codebythura.fruit2048.repository.DataStoreRepositoryImpl
import com.codebythura.fruit2048.repository.GameStateRepository
import com.codebythura.fruit2048.repository.GameStateRepositoryImpl
import com.codebythura.fruit2048.ui.home.GameViewModel
import com.codebythura.fruit2048.ui.landing.LandingViewModel
import com.codebythura.fruit2048.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Platform-agnostic DI: repositories and ViewModels. Their platform dependencies
 * (DataStore, SoundManager, VibrationManager, ads, …) come from each platform's
 * `platformModule`, wired together in [initKoin].
 */
val commonModule = module {
    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }
    single<GameStateRepository> { GameStateRepositoryImpl(get()) }

    viewModel { GameViewModel(get(), get(), get(), get(), get()) }
    viewModel { LandingViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
