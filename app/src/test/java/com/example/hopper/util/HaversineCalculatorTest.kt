package com.example.hopper.util

import com.example.hopper.domain.model.LatLng
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HaversineCalculatorTest {

    @Test
    fun `same point returns zero distance`() {
        val point = LatLng(22.5726, 88.3639) // Kolkata
        val distance = HaversineCalculator.distanceMeters(point, point)
        assertThat(distance).isEqualTo(0.0)
    }

    @Test
    fun `known distance between Kolkata and Chandannagar`() {
        // Kolkata (Victoria Memorial) to Chandannagar (Strand)
        val kolkata = LatLng(22.5448, 88.3426)
        val chandannagar = LatLng(22.8671, 88.3674)
        val distance = HaversineCalculator.distanceMeters(kolkata, chandannagar)
        // Expected ~35.8 km — verify within 500m tolerance for known coordinates
        assertThat(distance).isWithin(500.0).of(35_800.0)
    }

    @Test
    fun `short distance between two nearby pandals`() {
        // Two points approximately 1 km apart in Kolkata
        val point1 = LatLng(22.5726, 88.3639)
        val point2 = LatLng(22.5816, 88.3639) // ~1 km north
        val distance = HaversineCalculator.distanceMeters(point1, point2)
        // 0.009 degrees latitude ≈ ~1000m
        assertThat(distance).isWithin(50.0).of(1000.0)
    }

    @Test
    fun `distanceKm returns value in kilometers`() {
        val point1 = LatLng(22.5726, 88.3639)
        val point2 = LatLng(22.5816, 88.3639)
        val distanceKm = HaversineCalculator.distanceKm(point1, point2)
        val distanceMeters = HaversineCalculator.distanceMeters(point1, point2)
        assertThat(distanceKm).isWithin(0.001).of(distanceMeters / 1000.0)
    }

    @Test
    fun `antipodal points return approximately half Earth circumference`() {
        val point1 = LatLng(0.0, 0.0)
        val point2 = LatLng(0.0, 180.0) // Antipodal on equator
        val distance = HaversineCalculator.distanceMeters(point1, point2)
        // Half circumference ≈ π * 6371000 ≈ 20015086m
        assertThat(distance).isWithin(100.0).of(20_015_086.0)
    }

    @Test
    fun `distance is symmetric`() {
        val a = LatLng(22.5726, 88.3639)
        val b = LatLng(22.8671, 88.3674)
        val distAB = HaversineCalculator.distanceMeters(a, b)
        val distBA = HaversineCalculator.distanceMeters(b, a)
        assertThat(distAB).isWithin(0.001).of(distBA)
    }
}
