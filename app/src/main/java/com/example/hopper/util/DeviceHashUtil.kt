package com.example.hopper.util

import android.content.Context
import android.provider.Settings
import java.security.MessageDigest

/**
 * Generates a privacy-preserving device identifier using SHA-256 hashing.
 *
 * The raw ANDROID_ID is never exposed or transmitted. Only the deterministic
 * SHA-256 hash is used to attribute crowd reports without storing PII.
 *
 * Privacy guarantees:
 * - No user registration required
 * - No PII collected or stored
 * - Same device always produces the same hash (deterministic)
 * - Hash cannot be reversed to recover the original ANDROID_ID
 */
object DeviceHashUtil {

    /**
     * Returns the SHA-256 hash of the device's ANDROID_ID as a lowercase hex string.
     *
     * @param context Application or Activity context for accessing secure settings
     * @return 64-character lowercase hexadecimal SHA-256 hash string
     */
    fun getDeviceHash(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""
        return sha256(androidId)
    }

    /**
     * Computes the SHA-256 hash of the given input string.
     *
     * @param input The string to hash
     * @return 64-character lowercase hexadecimal hash string
     */
    internal fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
