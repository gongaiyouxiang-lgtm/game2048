package com.codebythura.fruit2048.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Anchored banner ad. Android: AdMob AdView; iOS: no-op (v1 ships without iOS ads). */
@Composable
expect fun BannerAd(modifier: Modifier = Modifier)
