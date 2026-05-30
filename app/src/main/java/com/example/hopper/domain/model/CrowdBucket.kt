package com.example.hopper.domain.model

/**
 * Three-tier crowd density classification representing estimated wait times at a pandal.
 */
enum class CrowdBucket(val label: String, val waitMinutes: Int) {
    GREEN("Under 10 min", 10),
    YELLOW("~25 min", 25),
    RED("60+ min", 60)
}
