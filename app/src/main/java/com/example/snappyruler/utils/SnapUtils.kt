package com.example.snappyruler.utils

import androidx.compose.ui.geometry.Offset
import com.example.snappyruler.model.Segment
import com.example.snappyruler.model.Shape
import kotlin.math.*

/**
 * Snapping utilities: grid, angle, and nearest-segment snapping.
 * Used by SnappyCanvas for ruler, protractor, compass, and HUD.
 */
object SnapUtils {

    fun distance(a: Offset, b: Offset): Float {
        return sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    }

    fun angleDegrees(a: Offset, b: Offset): Float {
        val dx = b.x - a.x
        val dy = b.y - a.y
        return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat().let { if (it < 0) it + 360f else it }
    }

    fun normalizeAngleDeg(a: Float): Float {
        var x = a % 360f
        if (x > 180f) x -= 360f
        if (x < -180f) x += 360f
        return x
    }

    fun snapAngle(angleDeg: Float, enabled: Boolean): Float {
        if (!enabled) return angleDeg
        val candidates = floatArrayOf(0f, 30f, 45f, 60f, 90f, 120f, 135f, 150f, 180f)
        var best = angleDeg
        var bestDiff = 360f
        for (c in candidates) {
            val d = abs(normalizeAngleDeg(angleDeg - c))
            if (d < bestDiff) {
                bestDiff = d
                best = c
            }
        }
        return if (bestDiff <= 3f) best else angleDeg
    }

    fun snapToGrid(p: Offset, gridSpacing: Float, enabled: Boolean): Offset {
        if (!enabled) return p
        val gx = (p.x / gridSpacing).roundToInt() * gridSpacing
        val gy = (p.y / gridSpacing).roundToInt() * gridSpacing
        return Offset(gx, gy)
    }

    fun projectPointToLine(a: Offset, b: Offset, p: Offset): Offset {
        val apx = p.x - a.x
        val apy = p.y - a.y
        val abx = b.x - a.x
        val aby = b.y - a.y
        val ab2 = abx * abx + aby * aby
        val t = if (ab2 == 0f) 0f else ((apx * abx + apy * aby) / ab2)
        return Offset(a.x + abx * t, a.y + aby * t)
    }

    fun closestPointOnSegments(p: Offset, segments: List<Segment>, radius: Float): Offset? {
        var best: Offset? = null
        var bestDist = radius
        for (s in segments) {
            val proj = projectPointToLine(s.start, s.end, p)
            val d = distance(p, proj)
            if (d < bestDist) {
                bestDist = d
                best = proj
            }
            val d1 = distance(p, s.start)
            if (d1 < bestDist) { bestDist = d1; best = s.start }
            val d2 = distance(p, s.end)
            if (d2 < bestDist) { bestDist = d2; best = s.end }
        }
        return best
    }

    /**
     * Applies snapping for start and end points.
     */
    fun applySnaps(
        start: Offset,
        current: Offset,
        shapes: List<Shape>,
        scale: Float,
        snapEnabled: Boolean
    ): Pair<Offset, Offset> {
        val baseRadius = 24f
        val snapRadius = (baseRadius / scale).coerceIn(8f, 80f)
        val gridSpacing = 40f * scale

        val segments = shapes.filterIsInstance<Segment>()
        val endSnap = closestPointOnSegments(current, segments, snapRadius)
        val startSnap = closestPointOnSegments(start, segments, snapRadius) ?: start

        var s = startSnap
        var e = endSnap ?: current

        val angle = angleDegrees(s, e)
        val snappedAngle = snapAngle(angle, snapEnabled)
        if (snappedAngle != angle) {
            val dist = distance(s, e)
            val rad = Math.toRadians(snappedAngle.toDouble())
            e = Offset((s.x + cos(rad) * dist).toFloat(), (s.y + sin(rad) * dist).toFloat())
        }

        e = snapToGrid(e, gridSpacing, snapEnabled)
        s = snapToGrid(s, gridSpacing, snapEnabled)

        return s to e
    }

    /**
     * Returns HUD string for ruler/line: angle + length
     */
    fun hudString(a: Offset, b: Offset): String {
        val ang = angleDegrees(a, b)
        val len = distance(a, b)
        return "Angle: ${"%.1f".format(ang)}°, Len: ${"%.1f".format(len)} px"
    }
}
