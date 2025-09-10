package com.example.snappyruler.util

import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.abs

/**
 * Calculate angle between two points in degrees.
 */
fun angleDegrees(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val angle = atan2((y2 - y1), (x2 - x1))
    return Math.toDegrees(angle.toDouble()).toFloat()
}

/**
 * Snap an angle to the nearest standard snap angle.
 */
fun snapAngle(angle: Float): Float {
    val snapAngles = listOf(0f, 30f, 45f, 60f, 90f, 120f, 135f, 150f, 180f)
    return snapAngles.minByOrNull { abs(it - angle) } ?: angle
}

/**
 * Format the HUD (Heads-Up Display) string showing length + angle.
 * @param lengthPx Length in pixels.
 * @param angle Angle in degrees.
 */
fun hudString(lengthPx: Float, angle: Float): String {
    // Assume 160 dpi → 1dp ≈ 1px, so 1 cm ≈ 37.8 px
    val lengthCm = lengthPx / 37.8f
    val roundedLength = String.format("%.1f cm", lengthCm)
    val roundedAngle = String.format("%.1f°", angle)
    return "Length: $roundedLength | Angle: $roundedAngle"
}

/**
 * Compute distance between two points.
 */
fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
}
