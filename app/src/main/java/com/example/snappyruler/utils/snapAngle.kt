package com.example.snappyruler.utils

fun snapAngle(angle: Float): Float {
    val snapAngles = listOf(0f, 30f, 45f, 60f, 90f, 120f, 135f, 150f, 180f)
    return snapAngles.minByOrNull { kotlin.math.abs(it - angle) } ?: angle
}
