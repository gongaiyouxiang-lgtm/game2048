package com.codebythura.fruit2048.util

/** Supported UI language BCP-47 tags (match the composeResources `values-*` qualifiers). */
val SUPPORTED_LANGUAGES = listOf("en", "zh-TW", "zh-CN")
const val DEFAULT_LANGUAGE = "en"

/**
 * In-app language switch.
 * Android: persists the tag and recreates the activity so resources re-resolve live.
 * iOS: persists AppleLanguages; the change applies on next launch.
 */
expect class LocaleController {
    fun current(): String
    fun set(tag: String)
}
