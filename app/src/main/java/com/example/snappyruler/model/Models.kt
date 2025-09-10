package com.example.snappyruler.model

import androidx.compose.ui.geometry.Offset
import kotlin.math.*

/**
 * Basic model types for shapes and previews.
 * copyForHistory returns a simple data-copy for undo history.
 */

sealed interface Shape {
    fun copyForHistory(): Shape
}

data class Segment(val start: Offset, val end: Offset) : Shape {
    override fun copyForHistory(): Shape = Segment(start.copy(), end.copy())
}

data class Freehand(val points: MutableList<Offset>) : Shape {
    override fun copyForHistory(): Shape = Freehand(points.toMutableList())
}

data class CircleShape(val center: Offset, val radius: Float) : Shape {
    override fun copyForHistory(): Shape = CircleShape(center.copy(), radius)
}

sealed interface Preview
data class PreviewLine(val start: Offset, val end: Offset) : Preview
data class PreviewCircle(val center: Offset, val radius: Float) : Preview
