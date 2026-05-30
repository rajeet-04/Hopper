package com.example.hopper.util

import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class StringProviderTest {

    private lateinit var preferences: SharedPreferences
    private lateinit var localeManager: LocaleManager
    private lateinit var stringProvider: StringProvider

    @Before
    fun setup() {
        preferences = mockk {
            every { getString(LocaleManager.KEY_LOCALE, null) } returns "en"
            every { edit() } returns mockk(relaxed = true)
        }
        localeManager = LocaleManager(preferences)
        stringProvider = StringProvider(localeManager)
    }

    // --- resolve() tests ---

    @Test
    fun `resolve returns English when locale is English`() {
        val result = stringProvider.resolve("Hello", "হ্যালো")
        assertThat(result).isEqualTo("Hello")
    }

    @Test
    fun `resolve returns Bengali when locale is Bengali`() {
        localeManager.setLocale(LocaleManager.LOCALE_BENGALI)
        val result = stringProvider.resolve("Hello", "হ্যালো")
        assertThat(result).isEqualTo("হ্যালো")
    }

    @Test
    fun `resolve falls back to English when Bengali is null and locale is Bengali`() {
        localeManager.setLocale(LocaleManager.LOCALE_BENGALI)
        val result = stringProvider.resolve("Hello", null)
        assertThat(result).isEqualTo("Hello")
    }

    @Test
    fun `resolve falls back to Bengali when English is null and locale is English`() {
        val result = stringProvider.resolve(null, "হ্যালো")
        assertThat(result).isEqualTo("হ্যালো")
    }

    @Test
    fun `resolve returns empty string when both are null`() {
        val result = stringProvider.resolve(null, null)
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `resolve returns English when Bengali is null and locale is English`() {
        val result = stringProvider.resolve("Hello", null)
        assertThat(result).isEqualTo("Hello")
    }

    // --- resolveNullable() tests ---

    @Test
    fun `resolveNullable returns English when locale is English`() {
        val result = stringProvider.resolveNullable("Hello", "হ্যালো")
        assertThat(result).isEqualTo("Hello")
    }

    @Test
    fun `resolveNullable returns Bengali when locale is Bengali`() {
        localeManager.setLocale(LocaleManager.LOCALE_BENGALI)
        val result = stringProvider.resolveNullable("Hello", "হ্যালো")
        assertThat(result).isEqualTo("হ্যালো")
    }

    @Test
    fun `resolveNullable falls back to English when Bengali is null and locale is Bengali`() {
        localeManager.setLocale(LocaleManager.LOCALE_BENGALI)
        val result = stringProvider.resolveNullable("Hello", null)
        assertThat(result).isEqualTo("Hello")
    }

    @Test
    fun `resolveNullable falls back to Bengali when English is null and locale is English`() {
        val result = stringProvider.resolveNullable(null, "হ্যালো")
        assertThat(result).isEqualTo("হ্যালো")
    }

    @Test
    fun `resolveNullable returns null when both are null`() {
        val result = stringProvider.resolveNullable(null, null)
        assertThat(result).isNull()
    }

    @Test
    fun `resolveNullable returns null when both are null and locale is Bengali`() {
        localeManager.setLocale(LocaleManager.LOCALE_BENGALI)
        val result = stringProvider.resolveNullable(null, null)
        assertThat(result).isNull()
    }
}
