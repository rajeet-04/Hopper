package com.example.hopper.util

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages app-wide locale state and persistence.
 * Handles dynamic language switching without requiring Activity restart.
 * Backed by SharedPreferences for persistence across app launches.
 */
@Singleton
class LocaleManager @Inject constructor(
    private val preferences: SharedPreferences
) {
    companion object {
        const val KEY_LOCALE = "app_locale"
        const val LOCALE_BENGALI = "bn"
        const val LOCALE_ENGLISH = "en"
    }

    private val _currentLocale = MutableStateFlow(getSavedLocale())
    val currentLocale: StateFlow<String> = _currentLocale.asStateFlow()

    /**
     * Persists and emits the new locale.
     * @param localeCode Must be "bn" or "en"
     */
    fun setLocale(localeCode: String) {
        preferences.edit().putString(KEY_LOCALE, localeCode).apply()
        _currentLocale.value = localeCode
    }

    /**
     * Returns the current locale code ("bn" or "en").
     */
    fun getLocale(): String = _currentLocale.value

    /**
     * Returns the persisted locale, falling back to system default on first launch.
     */
    fun getSavedLocale(): String {
        return preferences.getString(KEY_LOCALE, null) ?: getSystemDefault()
    }

    /**
     * Detects if the device's system locale is Bengali.
     */
    fun isSystemBengali(): Boolean {
        return Locale.getDefault().language == LOCALE_BENGALI
    }

    /**
     * Returns true if the current active locale is Bengali.
     */
    fun isBengali(): Boolean = _currentLocale.value == LOCALE_BENGALI

    private fun getSystemDefault(): String {
        return if (isSystemBengali()) LOCALE_BENGALI else LOCALE_ENGLISH
    }
}
