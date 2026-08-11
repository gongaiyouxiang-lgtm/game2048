package com.codebythura.fruit2048.ads

/**
 * Interstitial ad controller with a frequency cap. Android: AdMob (shows over the current
 * activity); iOS: no-op (v1 ships without iOS ads).
 */
expect class InterstitialAdManager {
    fun preload()
    /** Call once per game over; shows an ad only when the frequency cap allows. */
    fun maybeShowOnGameOver()
}
