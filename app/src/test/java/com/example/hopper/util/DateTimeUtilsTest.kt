package com.example.hopper.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Duration
import java.time.Instant

class DateTimeUtilsTest {

    @Test
    fun `fromEpochMillis converts correctly`() {
        val millis = 1700000000000L
        val instant = DateTimeUtils.fromEpochMillis(millis)
        assertThat(instant).isEqualTo(Instant.ofEpochMilli(millis))
    }

    @Test
    fun `toEpochMillis converts correctly`() {
        val instant = Instant.ofEpochMilli(1700000000000L)
        val millis = DateTimeUtils.toEpochMillis(instant)
        assertThat(millis).isEqualTo(1700000000000L)
    }

    @Test
    fun `roundtrip epoch conversion is lossless`() {
        val original = 1698765432100L
        val result = DateTimeUtils.toEpochMillis(DateTimeUtils.fromEpochMillis(original))
        assertThat(result).isEqualTo(original)
    }

    @Test
    fun `formatTimeAgo shows just now for recent times`() {
        val now = Instant.now()
        val past = now.minusSeconds(30)
        assertThat(DateTimeUtils.formatTimeAgo(past, now)).isEqualTo("just now")
    }

    @Test
    fun `formatTimeAgo shows minutes`() {
        val now = Instant.now()
        val past = now.minus(Duration.ofMinutes(5))
        assertThat(DateTimeUtils.formatTimeAgo(past, now)).isEqualTo("5 min ago")
    }

    @Test
    fun `formatTimeAgo shows singular minute`() {
        val now = Instant.now()
        val past = now.minus(Duration.ofMinutes(1))
        assertThat(DateTimeUtils.formatTimeAgo(past, now)).isEqualTo("1 min ago")
    }

    @Test
    fun `formatTimeAgo shows hours`() {
        val now = Instant.now()
        val past = now.minus(Duration.ofHours(3))
        assertThat(DateTimeUtils.formatTimeAgo(past, now)).isEqualTo("3 hrs ago")
    }

    @Test
    fun `formatTimeAgo shows singular hour`() {
        val now = Instant.now()
        val past = now.minus(Duration.ofHours(1))
        assertThat(DateTimeUtils.formatTimeAgo(past, now)).isEqualTo("1 hr ago")
    }

    @Test
    fun `formatTimeAgo shows days`() {
        val now = Instant.now()
        val past = now.minus(Duration.ofDays(2))
        assertThat(DateTimeUtils.formatTimeAgo(past, now)).isEqualTo("2 days ago")
    }

    @Test
    fun `isExpired returns true for past expiration`() {
        val now = Instant.now()
        val expired = now.minusSeconds(60)
        assertThat(DateTimeUtils.isExpired(expired, now)).isTrue()
    }

    @Test
    fun `isExpired returns false for future expiration`() {
        val now = Instant.now()
        val future = now.plusSeconds(60)
        assertThat(DateTimeUtils.isExpired(future, now)).isFalse()
    }

    @Test
    fun `expiresAfterMinutes adds correct duration`() {
        val start = Instant.ofEpochMilli(1700000000000L)
        val expiry = DateTimeUtils.expiresAfterMinutes(start, 20)
        assertThat(expiry).isEqualTo(start.plus(Duration.ofMinutes(20)))
    }
}
