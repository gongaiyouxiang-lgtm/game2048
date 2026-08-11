@file:OptIn(ExperimentalForeignApi::class)

package com.codebythura.fruit2048.util

import com.codebythura.fruit2048.repository.DataStoreRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

/**
 * iOS sound effects via [AVAudioPlayer]. The mp3s ship in the app bundle (added to the
 * iosApp Xcode target). Self-gated on the sound setting, mirroring the Android SoundManager.
 */
actual class SoundManager(
    dataStoreRepo: DataStoreRepository,
) {
    private var enabled = true
    private val players = mutableMapOf<String, AVAudioPlayer?>()

    init {
        dataStoreRepo.observeSoundEnabled()
            .onEach { enabled = it }
            .launchIn(CoroutineScope(Dispatchers.Default))
        listOf("merge", "slide", "gameover", "win", "click").forEach { name ->
            players[name] = loadPlayer(name)
        }
    }

    private fun loadPlayer(name: String): AVAudioPlayer? {
        val path = NSBundle.mainBundle.pathForResource(name, "mp3") ?: return null
        return try {
            AVAudioPlayer(contentsOfURL = NSURL.fileURLWithPath(path), error = null)
                .apply { prepareToPlay() }
        } catch (t: Throwable) {
            null
        }
    }

    private fun play(name: String) {
        if (!enabled) return
        players[name]?.let { player ->
            player.currentTime = 0.0
            player.play()
        }
    }

    // Pitch shifting on merge is an Android-only nicety; iOS just plays the merge sound.
    actual fun playMerge(rate: Float) = play("merge")
    actual fun playSlide() = play("slide")
    actual fun playGameOver() = play("gameover")
    actual fun playWin() = play("win")
    actual fun playClick() = play("click")
}
