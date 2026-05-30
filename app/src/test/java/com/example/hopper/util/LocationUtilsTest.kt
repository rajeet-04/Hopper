package com.example.hopper.util

import com.example.hopper.domain.model.LatLng
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LocationUtilsTest {

    @Test
    fun `isWithinRadius returns true for point inside radius`() {
        val center = LatLng(22.5726, 88.3639)
        val nearby = LatLng(22.5730, 88.3640) // ~45m away
        assertThat(LocationUtils.isWithinRadius(nearby, center, 100.0)).isTrue()
    }

    @Test
    fun `isWithinRadius returns false for point outside radius`() {
        val center = LatLng(22.5726, 88.3639)
        val far = LatLng(22.5826, 88.3639) // ~1.1km away
        assertThat(LocationUtils.isWithinRadius(far, center, 500.0)).isFalse()
    }

    @Test
    fun `isWithinRadius returns true for point exactly at boundary`() {
        val center = LatLng(0.0, 0.0)
        val point = LatLng(0.0, 0.0) // same point, distance = 0
        assertThat(LocationUtils.isWithinRadius(point, center, 0.0)).isTrue()
    }

    @Test
    fun `formatDistance shows meters for short distances`() {
        assertThat(LocationUtils.formatDistance(450.0)).isEqualTo("450 m")
    }

    @Test
    fun `formatDistance shows km for long distances`() {
        assertThat(LocationUtils.formatDistance(2300.0)).isEqualTo("2.3 km")
    }

    @Test
    fun `formatDistance shows meters at 999`() {
        assertThat(LocationUtils.formatDistance(999.0)).isEqualTo("999 m")
    }

    @Test
    fun `formatDistance shows km at 1000`() {
        assertThat(LocationUtils.formatDistance(1000.0)).isEqualTo("1.0 km")
    }

    @Test
    fun `estimateWalkingMinutes calculates correctly`() {
        // 1000m at 5 km/h = 12 minutes
        val minutes = LocationUtils.estimateWalkingMinutes(1000.0)
        assertThat(minutes).isEqualTo(12)
    }

    @Test
    fun `estimateWalkingMinutes rounds up`() {
        // 100m at 5 km/h = 1.2 minutes → rounds up to 2
        val minutes = LocationUtils.estimateWalkingMinutes(100.0)
        assertThat(minutes).isEqualTo(2)
    }

    @Test
    fun `isValidCoordinate accepts valid coordinates`() {
        assertThat(LocationUtils.isValidCoordinate(LatLng(22.5726, 88.3639))).isTrue()
        assertThat(LocationUtils.isValidCoordinate(LatLng(-90.0, -180.0))).isTrue()
        assertThat(LocationUtils.isValidCoordinate(LatLng(90.0, 180.0))).isTrue()
        assertThat(LocationUtils.isValidCoordinate(LatLng(0.0, 0.0))).isTrue()
    }

    @Test
    fun `isValidCoordinate rejects invalid coordinates`() {
        assertThat(LocationUtils.isValidCoordinate(LatLng(91.0, 0.0))).isFalse()
        assertThat(LocationUtils.isValidCoordinate(LatLng(-91.0, 0.0))).isFalse()
        assertThat(LocationUtils.isValidCoordinate(LatLng(0.0, 181.0))).isFalse()
        assertThat(LocationUtils.isValidCoordinate(LatLng(0.0, -181.0))).isFalse()
    }

    @Test
    fun `sortByDistance returns points in ascending distance order`() {
        val origin = LatLng(22.5726, 88.3639)
        val near = LatLng(22.5730, 88.3640)   // ~45m
        val mid = LatLng(22.5776, 88.3639)    // ~555m
        val far = LatLng(22.5826, 88.3639)    // ~1.1km

        val sorted = LocationUtils.sortByDistance(listOf(far, near, mid), origin)
        assertThat(sorted).isEqualTo(listOf(near, mid, far))
    }

    @Test
    fun `findNearest returns closest point`() {
        val origin = LatLng(22.5726, 88.3639)
        val near = LatLng(22.5730, 88.3640)
        val far = LatLng(22.5826, 88.3639)

        val nearest = LocationUtils.findNearest(origin, listOf(far, near))
        assertThat(nearest).isEqualTo(near)
    }

    @Test
    fun `findNearest returns null for empty list`() {
        val origin = LatLng(22.5726, 88.3639)
        assertThat(LocationUtils.findNearest(origin, emptyList())).isNull()
    }
}
