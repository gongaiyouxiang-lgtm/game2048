package com.codebythura.fruit2048.util

/**
 * Plays short game sound effects. Self-gated on the sound setting so callers just invoke a
 * play method. Android: SoundPool; iOS: AVAudioPlayer.
 */
expect class SoundManager {
    /** Merge sound; [rate] lets the pitch rise with the merged tile value. */
    fun playMerge(rate: Float = 1f)
    fun playSlide()
    fun playGameOver()
    fun playWin()
    fun playClick()
}
