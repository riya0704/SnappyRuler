package com.example.snappyruler.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.snappyruler.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

enum class ToolType { PEN, RULER, SETSQUARE45, SETSQUARE3060, PROTRACTOR, COMPASS }

class ToolViewModel : ViewModel() {
    // Render state
    val shapes = mutableListOf<Shape>()

    // Interaction state
    var activeTool: ToolType = ToolType.PEN
    var activePreview: Preview? = null
    var hudText: String? = null

    // Undo/Redo
    private val undoStack = ArrayDeque<List<Shape>>()
    private val redoStack = ArrayDeque<List<Shape>>()
    private val MAX_HISTORY = 20

    // Export provider (populated by UI)
    var exportImageProvider: (() -> Bitmap)? = null

    // Snap toggle
    var snapEnabled = true
    private var savedSnapState = true

    fun selectTool(t: ToolType) {
        activeTool = t
        activePreview = null
    }

    fun toggleTempSnap() {
        savedSnapState = snapEnabled
        snapEnabled = !snapEnabled
    }

    fun restoreSnap() {
        snapEnabled = savedSnapState
    }

    fun addShape(s: Shape) {
        pushHistory()
        shapes.add(s)
    }

    fun pushHistory() {
        if (undoStack.size >= MAX_HISTORY) undoStack.removeFirst()
        // store snapshot (shallow copy ok for demo)
        undoStack.addLast(shapes.map { it.copyForHistory() })
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val prev = undoStack.removeLast()
        redoStack.addLast(shapes.map { it.copyForHistory() })
        shapes.clear()
        shapes.addAll(prev)
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val next = redoStack.removeLast()
        pushHistory()
        shapes.clear()
        shapes.addAll(next)
    }

    fun exportBitmap(): Bitmap? = exportImageProvider?.invoke()
}
