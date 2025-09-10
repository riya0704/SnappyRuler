# Snappy Ruler Set (Android, Kotlin + Jetpack Compose)

This is a simplified implementation of the **Snappy Ruler Set** Android task.
Features implemented:
- Freehand drawing on a zoomable/pannable canvas.
- Ruler tool: drag, rotate, draw straight lines along edge; snapping to common angles and points.
- Set squares (45° and 30°–60°) selectable as tools.
- Protractor: measure angle at a vertex and readout with snapping.
- Compass (basic): set radius, draw circle.
- Snapping: grid (configurable), endpoints and midpoints of existing segments, angles (0,30,45,60,90,120,135,150,180).
- Dynamic snap radius based on zoom.
- Long-press toggles snapping temporarily.
- Precision HUD: shows current angle and length.
- Undo/Redo (20 steps).
- Export drawing to PNG.
- Clean separation: models, viewmodel, rendering.

**Notes**
- This is a simplified, well-structured codebase intended to be imported into Android Studio (Arctic Fox or newer).
- For calibration we assume 160dpi -> 1dp ≈ 1px. See `README_CALIBRATION` for details.

## How to open
1. Open this folder (`snappy_ruler_project`) in Android Studio.
2. Let Gradle sync.
3. Run on an Android device or emulator.

# SnappyRuler
