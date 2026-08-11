package com.codebythura.fruit2048.util

import android.content.Context

/**
 * Android language switch: persists the tag via [LocaleManager] and recreates the current
 * activity so every `stringResource` re-resolves against the matching resources.
 */
actual class LocaleController(private val context: Context) {
    actual fun current(): String = LocaleManager.getLanguage(context)

    actual fun set(tag: String) {
        LocaleManager.setLanguage(context, tag)
        ActivityHolder.current?.recreate()
    }
}
