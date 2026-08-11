package com.codebythura.fruit2048.util

/**
 * Short haptic feedback on tile merges. Callers decide whether vibration is enabled before
 * invoking. Android: Vibrator; iOS: UIImpactFeedbackGenerator.
 */
expect class VibrationManager {
    fun vibrateMerge()
}
