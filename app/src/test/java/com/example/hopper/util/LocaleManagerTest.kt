package com.example.hopper.util

import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LocaleManagerTest {

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var localeManager: LocaleManager

    @Before
    fun setup() {
        editor = mockk(relaxed = true)
        preferences = mockk {
            every { getString(LocaleManager.KEY_LOCALE, null) } returns null
            every { edit() } returns editor
        }
        every { editor.putString(any(), any()) } returns editor
    }

    @Test
    fun `defaults to English when system locale is not Bengali`() {
        // Default system locale in test is English
        localeManager = LocaleManager(preferences)
        assertThat(localeManager.getLocale()).isEqualTo(LocaleManager.LOCALE_ENGLISH)
    }

    @Test
    fun `returns saved locale from preferences`() {
        every { preferences.getString(LocaleManager.KEY_LOCALE, null) } returns "bn"
        localeManager = LocaleManager(preferences)
        assertThat(localeManager.getLocale()).isEqualTo(LocaleManager.LOCALE_BENGALI)
    }

    @Test
    fun `setLocale persists to SharedPreferences`() {
        localeManager = LocaleManager(preferences)
        localeManager.setLocale(LocaleManager.LOCALE_BENGALI)

        verify { editor.putString(LocaleManager.KEY_LOCALE, LocaleManager.LOCALE_BENGALI) }
        verify { editor.apply() }
    }

    @Test
    fun `setLocale updates StateFlow value`() {
        localeManager = LocaleManager(preferences)
        localeManager.setLocale(LocaleManager.LOCALE_BENGALI)

        assertThat(localeManager.currentLocale.value).isEqualTo(LocaleManager.LOCALE_BENGALI)
    }

    @Test
    fun `isBengali returns true when locale is bn`() {
        every { preferences.getString(LocaleManager.KEY_LOCALE, null) } returns "bn"
        localeManager = LocaleManager(preferences)

        assertThat(localeManager.isBengali()).isTrue()
    }

    @Test
    fun `isBengali returns false when locale is en`() {
        every { preferences.getString(LocaleManager.KEY_LOCALE, null) } returns "en"
        localeManager = LocaleManager(preferences)

        assertThat(localeManager.isBengali()).isFalse()
    }

    @Test
    fun `getLocale returns current StateFlow value`() {
        every { preferences.getString(LocaleManager.KEY_LOCALE, null) } returns "en"
        localeManager = LocaleManager(preferences)

        assertThat(localeManager.getLocale()).isEqualTo(LocaleManager.LOCALE_ENGLISH)

        localeManager.setLocale(LocaleManager.LOCALE_BENGALI)
        assertThat(localeManager.getLocale()).isEqualTo(LocaleManager.LOCALE_BENGALI)
    }

    @Test
    fun `getSavedLocale returns persisted value`() {
        every { preferences.getString(LocaleManager.KEY_LOCALE, null) } returns "bn"
        localeManager = LocaleManager(preferences)

        assertThat(localeManager.getSavedLocale()).isEqualTo(LocaleManager.LOCALE_BENGALI)
    }
}
