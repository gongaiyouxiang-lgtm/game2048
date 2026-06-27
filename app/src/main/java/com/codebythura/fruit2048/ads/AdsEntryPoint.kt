package com.codebythura.fruit2048.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/** Exposes ad singletons to non-injectable call sites (e.g. Composables). */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AdsEntryPoint {
    fun interstitialAdManager(): InterstitialAdManager
}

@Composable
fun rememberInterstitialAdManager(): InterstitialAdManager {
    val appContext = LocalContext.current.applicationContext
    return remember {
        EntryPointAccessors.fromApplication(appContext, AdsEntryPoint::class.java)
            .interstitialAdManager()
    }
}
