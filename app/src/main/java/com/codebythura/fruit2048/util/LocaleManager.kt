package com.codebythura.fruit2048.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Lightweight in-app language switcher that does not depend on AppCompat.
 *
 * The selected language tag is stored in SharedPreferences so it can be read
 * synchronously from [android.app.Activity.attachBaseContext] (before Hilt
 * injection is available). On change, the activity is recreated so every
 * `stringResource` re-resolves against the matching `values-*` resources.
 */
object LocaleManager {

    private const val PREFS = "app_settings"
    private const val KEY_LANGUAGE = "language"

    const val DEFAULT_LANGUAGE = "en"

    /** BCP-47 language tags backed by res qualifiers values / values-zh-rTW / values-zh-rCN. */
    val SUPPORTED_LANGUAGES = listOf("en", "zh-TW", "zh-CN")

    fun getLanguage(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE

    fun setLanguage(context: Context, languageTag: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, languageTag)
            .apply()
    }

    /** Returns a context whose resources resolve to the stored language. */
    fun applyLocale(context: Context): Context {
        val locale = Locale.forLanguageTag(getLanguage(context))
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
