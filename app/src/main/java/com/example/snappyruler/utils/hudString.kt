package com.example.snappyruler.utils

fun hudString(lengthPx: Float, angle: Float): String {
    // Assume 160 dpi → 1dp ≈ 1px, 1cm ≈ 37.8px
    val lengthCm = lengthPx / 37.8f
    val roundedLength = String.format("%.1f cm", lengthCm)
    val roundedAngle = String.format("%.1f°", angle)
    return "Length: $roundedLength | Angle: $roundedAngle"
}
