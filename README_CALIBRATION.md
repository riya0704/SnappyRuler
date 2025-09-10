# Calibration and Measurement

Length units are displayed in centimeters assuming screen density 160 dpi:
- 1 inch = 160 dp (standard baseline)
- 1 cm = 160 / 2.54 ≈ 62.99 dp
We use `pixelsPerCm = density * (160.0 / 160.0) * (1.0 / 2.54)` in code; effectively 1 cm ≈ 63 dp for a 160 dpi baseline.
If you want exact calibration for your device, adjust `pixelsPerCm` in `utils/Constants.kt`.
