package com.example.hopper.util

import java.time.Duration
import java.time.Instant

/**
 * Utility functions for date/time conversions and formatting.
 *
 * Uses java.time APIs (available on minSdk 26+) for epoch/Instant conversions
 * and relative time formatting used in crowd report staleness indicators.
 */
object DateTimeUtils {

    /**
     * Converts epoch milliseconds to a java.time.Instant.
     *
     * @param epochMillis Milliseconds since Unix epoch
     * @return Corresponding Instant
     */
    fun fromEpochMillis(epochMillis: Long): Instant {
        return Instant.ofEpochMilli(epochMillis)
    }

    /**
     * Converts a java.time.Instant to epoch milliseconds.
     *
     * @param instant The Instant to convert
     * @return Milliseconds since Unix epoch
     */
    fun toEpochMillis(instant: Instant): Long {
        return instant.toEpochMilli()
    }

    /**
     * Returns the current time as an Instant.
     */
    fun now(): Instant {
        return Instant.now()
    }

    /**
     * Formats the duration between a past instant and now as a human-readable
     * relative time string (e.g., "2 min ago", "1 hr ago").
     *
     * @param past The past Instant to compare against now
     * @param now The reference "current" time (defaults to Instant.now())
     * @return Human-readable relative time string
     */
    fun formatTimeAgo(past: Instant, now: Instant = Instant.now()): String {
        val duration = Duration.between(past, now)
        val seconds = duration.seconds

        return when {
            seconds < 0 -> "just now"
            seconds < 60 -> "just now"
            seconds < 3600 -> {
                val minutes = seconds / 60
                if (minutes == 1L) "1 min ago" else "$minutes min ago"
            }
            seconds < 86400 -> {
                val hours = seconds / 3600
                if (hours == 1L) "1 hr ago" else "$hours hrs ago"
            }
            else -> {
                val days = seconds / 86400
                if (days == 1L) "1 day ago" else "$days days ago"
            }
        }
    }

    /**
     * Checks whether the given instant has expired relative to the current time.
     *
     * @param expiresAt The expiration instant
     * @param now The reference "current" time (defaults to Instant.now())
     * @return true if the instant is in the past (expired)
     */
    fun isExpired(expiresAt: Instant, now: Instant = Instant.now()): Boolean {
        return now.isAfter(expiresAt)
    }

    /**
     * Creates an expiration instant by adding the specified minutes to the given start time.
     *
     * @param from The starting instant
     * @param minutes Number of minutes until expiration
     * @return The expiration Instant
     */
    fun expiresAfterMinutes(from: Instant, minutes: Long): Instant {
        return from.plus(Duration.ofMinutes(minutes))
    }
}
