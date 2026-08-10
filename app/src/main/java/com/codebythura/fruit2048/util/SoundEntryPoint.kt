package com.codebythura.fruit2048.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/** Exposes the [SoundManager] singleton to Composables for UI click sounds. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface SoundEntryPoint {
    fun soundManager(): SoundManager
}

@Composable
fun rememberSoundManager(): SoundManager {
    val appContext = LocalContext.current.applicationContext
    return remember {
        EntryPointAccessors.fromApplication(appContext, SoundEntryPoint::class.java).soundManager()
    }
}
