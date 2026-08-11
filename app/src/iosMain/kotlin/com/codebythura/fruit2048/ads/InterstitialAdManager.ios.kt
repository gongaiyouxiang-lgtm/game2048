package com.codebythura.fruit2048.ads

/** iOS ships without ads in v1. */
actual class InterstitialAdManager {
    actual fun preload() {}
    actual fun maybeShowOnGameOver() {}
}
