package com.codebythura.fruit2048.util

import android.media.AudioManager
import android.media.ToneGenerator
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Plays short game sound effects. Uses [ToneGenerator] so no audio asset needs
 * to be bundled; this can later be swapped for SoundPool + real audio files.
 * Callers decide whether sound is enabled before invoking these methods.
 */
@Singleton
class SoundManager @Inject constructor() {

    private var toneGenerator: ToneGenerator? = null

    private fun generator(): ToneGenerator? {
        if (toneGenerator == null) {
            toneGenerator = try {
                ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            } catch (e: RuntimeException) {
                null
            }
        }
        return toneGenerator
    }

    fun playMerge() {
        try {
            generator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        } catch (e: RuntimeException) {
            // ToneGenerator can fail to allocate on some devices; ignore.
        }
    }
}
