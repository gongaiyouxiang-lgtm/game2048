package com.codebythura.fruit2048.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.codebythura.fruit2048.R
import com.codebythura.fruit2048.repository.DataStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Plays short game sound effects via [SoundPool] (low latency, overlapping playback).
 * Self-gated: reads the sound setting so callers just call the play methods.
 */
class SoundManager(
    context: Context,
    dataStoreRepo: DataStoreRepository,
) {
    @Volatile
    private var enabled = true

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val mergeId = soundPool.load(context, R.raw.merge, 1)
    private val slideId = soundPool.load(context, R.raw.slide, 1)
    private val gameOverId = soundPool.load(context, R.raw.gameover, 1)
    private val winId = soundPool.load(context, R.raw.win, 1)
    private val clickId = soundPool.load(context, R.raw.click, 1)

    init {
        dataStoreRepo.observeSoundEnabled()
            .onEach { enabled = it }
            .launchIn(CoroutineScope(Dispatchers.Default))
    }

    private fun play(soundId: Int, rate: Float = 1f) {
        if (enabled && soundId != 0) {
            soundPool.play(soundId, 1f, 1f, 1, 0, rate)
        }
    }

    /** Merge sound; [rate] lets the pitch rise with the merged tile value. */
    fun playMerge(rate: Float = 1f) = play(mergeId, rate)
    fun playSlide() = play(slideId)
    fun playGameOver() = play(gameOverId)
    fun playWin() = play(winId)
    fun playClick() = play(clickId)
}
