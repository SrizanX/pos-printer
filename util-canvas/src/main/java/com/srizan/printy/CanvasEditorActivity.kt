package com.srizan.printy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.srizan.printy.feature.canvas.CanvasScreen
import com.srizan.printy.ui.theme.PosprinterTheme

/**
 * Activity that wraps the canvas editor and returns a dithered bitmap result.
 *
 * Usage:
 * ```kotlin
 * val canvasLauncher = registerForActivityResult(CanvasEditorContract()) { bitmap ->
 *     if (bitmap != null) {
 *         // Use the dithered bitmap for printing
 *         Printer.printImage(bitmap, PrinterAlignment.CENTER)
 *     }
 * }
 *
 * // Launch
 * canvasLauncher.launch(Unit)
 * ```
 */
class CanvasEditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PosprinterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CanvasScreen(
                        modifier = Modifier.padding(innerPadding),
                        onCanvasReady = { bitmap ->
                            // Store bitmap in companion object singleton (avoids Parcel serialization)
                            lastResultBitmap = bitmap
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    )
                }
            }
        }
    }

    companion object {
        // Temporary storage for bitmap result (cleared after retrieval)
        private var lastResultBitmap: Bitmap? = null

        fun getIntent(context: Context): Intent = Intent(context, CanvasEditorActivity::class.java)

        fun getAndClearResult(): Bitmap? = lastResultBitmap.also { lastResultBitmap = null }
    }
}

/**
 * Activity result contract for launching the canvas editor.
 *
 * Returns: A dithered Bitmap with all canvas elements rendered, or null if canceled.
 */
class CanvasEditorContract : ActivityResultContract<Unit, Bitmap?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return CanvasEditorActivity.getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
        return if (resultCode == Activity.RESULT_OK) {
            // Retrieve bitmap from companion object singleton (avoids Parcel serialization)
            CanvasEditorActivity.getAndClearResult()
        } else {
            null
        }
    }
}

