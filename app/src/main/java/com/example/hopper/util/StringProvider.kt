package com.example.hopper.util

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides localized strings for domain/data layers that don't have
 * direct access to Android Context resources.
 * Resolves bilingual fields (name vs nameBengali) based on current locale.
 */
@Singleton
class StringProvider @Inject constructor(
    private val localeManager: LocaleManager
) {

    /**
     * Resolves a bilingual field pair based on the active locale.
     * Returns Bengali if locale is "bn" and Bengali text is non-null,
     * otherwise returns English. Falls back to empty string if both are null.
     */
    fun resolve(english: String?, bengali: String?): String {
        return if (localeManager.isBengali()) {
            bengali ?: english ?: ""
        } else {
            english ?: bengali ?: ""
        }
    }

    /**
     * Same as [resolve] but allows null return when both fields are null.
     * Returns Bengali if locale is "bn" and Bengali text is non-null,
     * otherwise returns English. Returns null only if both inputs are null.
     */
    fun resolveNullable(english: String?, bengali: String?): String? {
        return if (localeManager.isBengali()) {
            bengali ?: english
        } else {
            english ?: bengali
        }
    }
}
