# PrintY Canvas Editor Library Integration

## Overview

The `app-printy` module has been converted to a library that can be integrated into any Android app. It provides a canvas editor activity that lets users compose images with text, stickers, and dither effects, then returns a final dithered bitmap for printing.

## How to Use

### 1. Launch the Canvas Editor

In your Activity, register the canvas editor result launcher:

```kotlin
import com.srizan.printy.CanvasEditorContract

class MainActivity : AppCompatActivity() {
    
    val canvasEditor = registerForActivityResult(CanvasEditorContract()) { bitmap ->
        if (bitmap != null) {
            // You now have the final dithered bitmap ready for printing
            Printer.printImage(bitmap, PrinterAlignment.CENTER)
        }
    }
    
    // ... rest of code
    
    fun openCanvasEditor() {
        canvasEditor.launch(Unit)  // Launch with Unit as input (no parameters)
    }
}
```

**Note:** The bitmap is stored in the library's singleton cache, so it bypasses Parcel serialization limits. This is automatically cleared after retrieval.

### 2. What Users Can Do in the Canvas Editor

- **Add Images**: Pick from gallery and add to canvas
- **Add Text**: Type text directly on canvas
- **Add Stickers**: Choose from emoji stickers (😀, ❤️, ⭐, ✅, 🔥, 🍔)
- **Transform Elements**: 
  - Drag to move
  - Pinch to scale
  - Rotate with two-finger gestures
- **Select Canvas Width**: Choose between 58mm or 80mm thermal paper width
- **Choose Dither Mode**: Apply different error diffusion algorithms:
  - Atkinson (default, high contrast)
  - Floyd-Steinberg (classic, smooth)
  - Burkes
  - Sierra
  - Sierra Lite (fast)
- **Export**: Click Export button to return the final dithered bitmap

### 3. Understanding the Result

The returned `Bitmap` is:
- Already dithered (converted to monochrome with the selected dithering algorithm)
- Sized exactly to the chosen paper width (384px for 58mm, 576px for 80mm)
- Ready for direct printing with ESC/POS commands
- Fully transparent regions blended onto white background

### 4. Integration Example

See `app/src/main/java/com/srizan/posprinter/MainActivity.kt` for a complete example showing:
- Registering the canvas editor contract
- Adding a menu item to launch it
- Displaying the returned bitmap in preview
- Using it with the existing printer mechanism

## Architecture

**Library Module**: `app-printy` (Jetpack Compose + Kotlin)
- `CanvasEditorActivity` - Entry point activity
- `CanvasScreen` - Main UI composable
- `CanvasViewModel` - State management
- `CanvasElement` - Data models (Image, Text, Sticker)
- `RenderEngine` / `GestureHandler` - Canvas operations

**Integration**: `app` (Android Framework + View Binding)
- Imports `CanvasEditorContract` from `app-printy`
- Uses activity result contract pattern
- Passes result bitmap to existing printing pipeline

## Building

```bash
# Build just the library
./gradlew :app-printy:build

# Build main app with linked library
./gradlew :app:build

# Run full app (includes canvas editor)
./gradlew :app:installDebug
```

## Technical Notes

### Bitmap Transfer Mechanism

Bitmaps are **too large to serialize** through Android's Parcel system. Instead of passing the bitmap through Intent extras, the library uses an **internal singleton cache**:

1. When user clicks Export, the bitmap is stored in `CanvasEditorActivity.lastResultBitmap`
2. The activity calls `setResult(RESULT_OK)` and finishes
3. When `parseResult()` is called in the contract, it retrieves the bitmap and **clears the cache**
4. This avoids `"Could not copy bitmap to parcel blob"` crashes

This approach is safe because:
- Result contracts guarantee single-threaded execution
- Cache is auto-cleared immediately after retrieval
- App memory is used efficiently (large bitmaps never serialized)

## Future Enhancements

- [ ] Undo/Redo support
- [ ] Layer ordering (bring to front / send back)
- [ ] Custom text colors and fonts
- [ ] More emoji sticker options
- [ ] Template / preset layouts
- [ ] Save/load compositions

