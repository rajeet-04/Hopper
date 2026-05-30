package com.example.hopper.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DeviceHashUtilTest {

    @Test
    fun `sha256 produces deterministic output for same input`() {
        val input = "test_android_id_12345"
        val hash1 = DeviceHashUtil.sha256(input)
        val hash2 = DeviceHashUtil.sha256(input)
        assertThat(hash1).isEqualTo(hash2)
    }

    @Test
    fun `sha256 produces 64 character hex string`() {
        val hash = DeviceHashUtil.sha256("any_input")
        assertThat(hash).hasLength(64)
    }

    @Test
    fun `sha256 produces lowercase hex characters only`() {
        val hash = DeviceHashUtil.sha256("test_input")
        assertThat(hash).matches("[0-9a-f]{64}")
    }

    @Test
    fun `sha256 produces different hashes for different inputs`() {
        val hash1 = DeviceHashUtil.sha256("device_a")
        val hash2 = DeviceHashUtil.sha256("device_b")
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun `sha256 of known input matches expected value`() {
        // SHA-256 of "hello" is well-known
        val hash = DeviceHashUtil.sha256("hello")
        assertThat(hash).isEqualTo(
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
        )
    }

    @Test
    fun `sha256 of empty string produces valid hash`() {
        val hash = DeviceHashUtil.sha256("")
        assertThat(hash).hasLength(64)
        // SHA-256 of empty string is well-known
        assertThat(hash).isEqualTo(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        )
    }
}
