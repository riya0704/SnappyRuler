package com.example.snappyruler.ui.canvas

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.example.snappyruler.model.*
import com.example.snappyruler.utils.SnapUtils
import com.example.snappyruler.viewmodel.ToolType
import com.example.snappyruler.viewmodel.ToolViewModel
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * A Composable canvas for drawing shapes with snapping, grid, and HUD.
 */
@Composable
fun SnappyCanvas(modifier: Modifier = Modifier, viewModel: ToolViewModel) {
    val density = LocalDensity.current
    var canvasSize by remember { mutableStateOf(IntSize(1,1)) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var currentPreview by remember { mutableStateOf<Preview?>(null) }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.25f, 4f)
                        offset += pan
                    }
                }
                .pointerInput(viewModel) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var longPressJob = kotlinx.coroutines.GlobalScope.launch {
                                kotlinx.coroutines.delay(600L)
                                viewModel.toggleTempSnap()
                            }

                            when (viewModel.activeTool) {
                                ToolType.PEN -> {
                                    val stroke = mutableListOf(down.position)
                                    viewModel.hudText = "Pen drawing"
                                    val pointerId = down.id
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                        if (change.changedToUpIgnoreConsumed()) {
                                            longPressJob.cancel()
                                            if (stroke.size >= 2) viewModel.addShape(Freehand(stroke.toMutableList()))
                                            viewModel.hudText = null
                                            currentPreview = null
                                            break
                                        } else {
                                            stroke.add(change.position)
                                            currentPreview = PreviewFreehand(stroke.toList())
                                            change.consume()
                                        }
                                    }
                                }
                                ToolType.RULER, ToolType.SETSQUARE45, ToolType.SETSQUARE3060 -> {
                                    var start = down.position
                                    var current = start
                                    viewModel.hudText = "Ruler"
                                    longPressJob.cancel()
                                    val pointerId = down.id
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                        if (change.changedToUpIgnoreConsumed()) {
                                            val snapped = SnapUtils.applySnaps(start, current, viewModel.shapes, scale, viewModel.snapEnabled)
                                            viewModel.addShape(Segment(snapped.first, snapped.second))
                                            currentPreview = null
                                            viewModel.hudText = null
                                            break
                                        } else {
                                            current = change.position
                                            val snapped = SnapUtils.applySnaps(start, current, viewModel.shapes, scale, viewModel.snapEnabled)
                                            currentPreview = PreviewLine(snapped.first, snapped.second)
                                            viewModel.hudText = SnapUtils.hudString(snapped.first, snapped.second)
                                            change.consume()
                                        }
                                    }
                                }
                                ToolType.PROTRACTOR -> {
                                    val vertex = down.position
                                    var current = vertex
                                    viewModel.hudText = "Protractor"
                                    longPressJob.cancel()
                                    val pointerId = down.id
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                        if (change.changedToUpIgnoreConsumed()) {
                                            val ang = SnapUtils.angleDegrees(vertex, current)
                                            viewModel.hudText = "Angle: ${"%.1f".format(SnapUtils.snapAngle(ang, viewModel.snapEnabled))}°"
                                            currentPreview = null
                                            break
                                        } else {
                                            current = change.position
                                            currentPreview = PreviewLine(vertex, current)
                                            val ang = SnapUtils.angleDegrees(vertex, current)
                                            viewModel.hudText = "Angle: ${"%.1f".format(SnapUtils.snapAngle(ang, viewModel.snapEnabled))}°"
                                            change.consume()
                                        }
                                    }
                                }
                                ToolType.COMPASS -> {
                                    val center = down.position
                                    var current = center
                                    viewModel.hudText = "Compass"
                                    longPressJob.cancel()
                                    val pointerId = down.id
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                        if (change.changedToUpIgnoreConsumed()) {
                                            val r = SnapUtils.distance(center, current)
                                            viewModel.addShape(CircleShape(center, r))
                                            currentPreview = null
                                            viewModel.hudText = null
                                            break
                                        } else {
                                            current = change.position
                                            currentPreview = PreviewCircle(center, SnapUtils.distance(center, current))
                                            viewModel.hudText = "R=${"%.1f".format(SnapUtils.distance(center,current))} px"
                                            change.consume()
                                        }
                                    }
                                }
                            }

                            viewModel.restoreSnap()
                        }
                    }
                }
                .onSizeChanged { canvasSize = it }
        ) {
            drawGrid(scale, offset, size)

            viewModel.shapes.forEach { shape ->
                when (shape) {
                    is Segment -> drawSegment(shape)
                    is Freehand -> drawFreehand(shape)
                    is CircleShape -> drawCircle(shape)
                }
            }

            currentPreview?.let { p ->
                when (p) {
                    is PreviewLine -> drawLine(Color.Red, p.start, p.end, strokeWidth = 3f)
                    is PreviewCircle -> drawCircle(Color.Red, p.radius, center = p.center, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                    is PreviewFreehand -> for (i in 1 until p.points.size) drawLine(Color.Red, p.points[i-1], p.points[i], strokeWidth = 3f)
                }
            }

            viewModel.hudText?.let { text ->
                drawContext.canvas.nativeCanvas.apply {
                    val paint = AndroidPaint().apply { color = android.graphics.Color.BLACK; textSize = 36f }
                    drawText(text, 20f, 50f, paint)
                }
            }
        }
    }

    viewModel.exportImageProvider = {
        val bmp = Bitmap.createBitmap(
            max(1, canvasSize.width),
            max(1, canvasSize.height),
            Bitmap.Config.ARGB_8888
        )
        val c = AndroidCanvas(bmp)

        // Fill background with white
        c.drawColor(android.graphics.Color.WHITE)

        val paint = AndroidPaint().apply {
            strokeWidth = 3f
            style = AndroidPaint.Style.STROKE
            color = android.graphics.Color.BLACK
        }

        viewModel.shapes.forEach { shape ->
            when (shape) {
                is Segment -> c.drawLine(shape.start.x, shape.start.y, shape.end.x, shape.end.y, paint)
                is Freehand -> for (i in 1 until shape.points.size)
                    c.drawLine(
                        shape.points[i - 1].x,
                        shape.points[i - 1].y,
                        shape.points[i].x,
                        shape.points[i].y,
                        paint
                    )
                is CircleShape -> c.drawCircle(shape.center.x, shape.center.y, shape.radius, paint)
            }
        }

        bmp
    }

}

// Drawing helpers
private fun DrawScope.drawGrid(scale: Float, offset: Offset, size: Size) {
    val spacing = 40f * scale
    val cols = (size.width / spacing).toInt() + 2
    val rows = (size.height / spacing).toInt() + 2
    for (i in -1..cols) {
        val x = i * spacing + offset.x % spacing
        drawLine(Color(0x11000000), Offset(x, 0f), Offset(x, size.height), 1f)
    }
    for (j in -1..rows) {
        val y = j * spacing + offset.y % spacing
        drawLine(Color(0x11000000), Offset(0f, y), Offset(size.width, y), 1f)
    }
}

private fun DrawScope.drawSegment(s: Segment) {
    drawLine(Color.Black, s.start, s.end, strokeWidth = 3f)
}

private fun DrawScope.drawFreehand(f: Freehand) {
    val pts = f.points
    for (i in 1 until pts.size) drawLine(Color.Black, pts[i-1], pts[i], strokeWidth = 3f)
}

private fun DrawScope.drawCircle(circ: CircleShape) {
    drawCircle(Color.Transparent, radius = circ.radius, center = circ.center, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
}

// Preview models
sealed interface Preview
data class PreviewLine(val start: Offset, val end: Offset) : Preview
data class PreviewCircle(val center: Offset, val radius: Float) : Preview
data class PreviewFreehand(val points: List<Offset>) : Preview
