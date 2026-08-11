package com.codebythura.fruit2048.util

import platform.Foundation.NSUserDefaults

/**
 * iOS language switch. Writes the choice to `AppleLanguages`; Compose Multiplatform resources
 * resolve against it on the next launch. (Live in-app switching is an Android-only feature for v1.)
 */
actual class LocaleController {

    private val defaults get() = NSUserDefaults.standardUserDefaults

    actual fun current(): String {
        val langs = defaults.arrayForKey("AppleLanguages")
        val first = langs?.firstOrNull() as? String ?: return DEFAULT_LANGUAGE
        return fromAppleTag(first)
    }

    actual fun set(tag: String) {
        defaults.setObject(listOf(toAppleTag(tag)), forKey = "AppleLanguages")
        defaults.synchronize()
    }

    private fun toAppleTag(tag: String): String = when (tag) {
        "zh-TW" -> "zh-Hant"
        "zh-CN" -> "zh-Hans"
        else -> "en"
    }

    private fun fromAppleTag(appleTag: String): String = when {
        appleTag.startsWith("zh-Hant") || appleTag.startsWith("zh-TW") -> "zh-TW"
        appleTag.startsWith("zh-Hans") || appleTag.startsWith("zh-CN") -> "zh-CN"
        else -> "en"
    }
}
