package com.codebythura.fruit2048.ads

import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.codebythura.fruit2048.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Adaptive anchored banner. Uses [BuildConfig.BANNER_AD_UNIT_ID] (test unit in
 * debug, real unit in release). No-op inside @Preview/inspection.
 */
@Composable
actual fun BannerAd(modifier: Modifier) {
    if (LocalInspectionMode.current) return

    val adWidthDp = LocalConfiguration.current.screenWidthDp

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                adUnitId = BuildConfig.BANNER_AD_UNIT_ID
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthDp)
                )
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                )
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
