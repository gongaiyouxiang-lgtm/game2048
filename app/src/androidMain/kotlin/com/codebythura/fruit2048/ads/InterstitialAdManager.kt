package com.codebythura.fruit2048.ads

import android.content.Context
import android.util.Log
import com.codebythura.fruit2048.BuildConfig
import com.codebythura.fruit2048.util.ActivityHolder
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Loads and shows interstitial ads with a frequency cap so they only appear on
 * every Nth game over and at most once per [MIN_INTERVAL_MILLIS]. Always keeps
 * one ad preloaded.
 */
actual class InterstitialAdManager(
    private val context: Context,
) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var gameOverCount = 0
    private var lastShownAtMillis = 0L

    actual fun preload() {
        if (interstitialAd != null || isLoading) return
        isLoading = true
        InterstitialAd.load(
            context,
            BuildConfig.INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial loaded")
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Interstitial failed to load: ${error.message}")
                    interstitialAd = null
                    isLoading = false
                }
            },
        )
    }

    /** Call once per game over; shows the ad only when the frequency cap allows. */
    actual fun maybeShowOnGameOver() {
        val activity = ActivityHolder.current
        gameOverCount++
        val now = System.currentTimeMillis()
        val capReady = gameOverCount % SHOW_EVERY_N_GAME_OVERS == 0 &&
            now - lastShownAtMillis >= MIN_INTERVAL_MILLIS
        val ad = interstitialAd
        if (!capReady || ad == null || activity == null) {
            Log.d(TAG, "Interstitial skipped (count=$gameOverCount, ready=$capReady, hasAd=${ad != null})")
            preload()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                preload()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.w(TAG, "Interstitial failed to show: ${error.message}")
                interstitialAd = null
                preload()
            }
        }
        lastShownAtMillis = now
        Log.d(TAG, "Showing interstitial (count=$gameOverCount)")
        ad.show(activity)
    }

    companion object {
        private const val TAG = "InterstitialAdManager"
        private const val SHOW_EVERY_N_GAME_OVERS = 3
        private const val MIN_INTERVAL_MILLIS = 60_000L
    }
}
