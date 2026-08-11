package com.codebythura.fruit2048.util

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

/** iOS merge haptic via [UIImpactFeedbackGenerator]. */
actual class VibrationManager {
    actual fun vibrateMerge() {
        val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
        generator.prepare()
        generator.impactOccurred()
    }
}
