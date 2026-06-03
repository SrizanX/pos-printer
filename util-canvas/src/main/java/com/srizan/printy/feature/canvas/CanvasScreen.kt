package com.srizan.printy.feature.canvas

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.srizan.util.ditherer.DitherKernel
import com.srizan.util.ditherer.DitherPresets
import com.srizan.util.ditherer.Ditherer

private data class DitherOption(val label: String, val kernel: DitherKernel)

private val ditherOptions = listOf(
    DitherOption("Atkinson", DitherPresets.atkinson),
    DitherOption("Floyd", DitherPresets.floydSteinberg),
    DitherOption("Burkes", DitherPresets.burkes),
    DitherOption("Sierra", DitherPresets.sierra),
    DitherOption("Lite", DitherPresets.sierraLite)
)

@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    onCanvasReady: ((Bitmap) -> Unit)? = null
) {
    val viewModel = remember { CanvasViewModel() }
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val density = LocalDensity.current
    val scaledDensity = density.density * density.fontScale
    var textInput by remember { mutableStateOf("") }
    var canvasSizePx by remember { mutableStateOf(IntSize.Zero) }
    var selectedDither by remember { mutableStateOf(ditherOptions.first()) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val bitmap = uri?.toImageBitmap(context) ?: return@rememberLauncherForActivityResult
        viewModel.addImage(bitmap)
    }

    val ditheredPreview = remember(state.elements, selectedDither, canvasSizePx) {
        if (canvasSizePx.width <= 0 || canvasSizePx.height <= 0) {
            null
        } else {
            val rendered = renderCanvasBitmap(
                elements = state.elements,
                canvasWidthPx = canvasSizePx.width,
                canvasHeightPx = canvasSizePx.height,
                density = density.density,
                scaledDensity = scaledDensity
            )
            Ditherer(
                kernel = selectedDither.kernel,
                targetWidth = canvasSizePx.width,
                threshold = 128
            ).dither(rendered)
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { imagePicker.launch("image/*") }) { Text("Add image") }
            Button(onClick = { viewModel.addText(textInput.ifBlank { "Text" }) }) { Text("Add text") }
            Button(onClick = { viewModel.deleteSelected() }, enabled = state.selectedId != null) { Text("Delete") }
        }

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Text to add") },
            singleLine = true
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("😀", "❤️", "⭐", "✅", "🔥", "🍔")) { emoji ->
                Button(onClick = { viewModel.addSticker(emoji) }) {
                    Text(emoji)
                }
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ditherOptions) { option ->
                FilterChip(
                    selected = selectedDither == option,
                    onClick = { selectedDither = option },
                    label = { Text(option.label) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PaperWidth.entries.forEach { width ->
                FilterChip(
                    selected = state.paperWidth == width,
                    onClick = { viewModel.setPaperWidth(width) },
                    label = { Text(width.label) }
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
        ) {
            val canvasWidth = if (state.paperWidth == PaperWidth.MM58) 220.dp else 300.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp)
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(width = canvasWidth, height = 400.dp)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFD0D0D0))
                        .onSizeChanged { canvasSizePx = it }
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { viewModel.select(null) })
                        }
                ) {
                    ditheredPreview?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "dithered canvas",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    state.elements.forEach { element ->
                        TransformableElement(
                            element = element,
                            selected = state.selectedId == element.id,
                            onSelect = { viewModel.select(element.id) },
                            onTransformDelta = { panX, panY, zoomDelta, rotationDelta, elementBaseWidthPx, elementBaseHeightPx ->
                                viewModel.applyTransformDelta(
                                    id = element.id,
                                    panX = panX,
                                    panY = panY,
                                    zoomDelta = zoomDelta,
                                    rotationDelta = rotationDelta,
                                    canvasWidthPx = canvasSizePx.width,
                                    canvasHeightPx = canvasSizePx.height,
                                    elementBaseWidthPx = elementBaseWidthPx,
                                    elementBaseHeightPx = elementBaseHeightPx
                                )
                            }
                        )
                    }
                }
            }
        }

        if (onCanvasReady != null) {
            Button(
                onClick = { ditheredPreview?.let { onCanvasReady(it) } },
                enabled = ditheredPreview != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export Canvas")
            }
        }

        Text(
            text = "Tip: choose a dither mode and drag to reposition elements before printing.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun TransformableElement(
    element: CanvasElement,
    selected: Boolean,
    onSelect: () -> Unit,
    onTransformDelta: (
        panX: Float,
        panY: Float,
        zoomDelta: Float,
        rotationDelta: Float,
        elementBaseWidthPx: Int,
        elementBaseHeightPx: Int
    ) -> Unit
) {
    val density = LocalDensity.current
    val (baseWidthPx, baseHeightPx) = estimateElementBoundsPx(element, density.density, density.fontScale)
    val transform = element.transform
    val borderColor = if (selected) Color(0xFF7F77DD) else Color.Transparent

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationX = transform.x
                translationY = transform.y
                scaleX = transform.scale
                scaleY = transform.scale
                rotationZ = transform.rotation
            }
            .size(
                width = with(density) { baseWidthPx.toDp() },
                height = with(density) { baseHeightPx.toDp() }
            )
            .clip(RoundedCornerShape(6.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(6.dp))
            .pointerInput(element.id) {
                detectTapGestures(onTap = { onSelect() })
            }
            .pointerInput(element.id) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    onSelect()
                    onTransformDelta(
                        pan.x,
                        pan.y,
                        zoom,
                        rotation,
                        baseWidthPx.toInt(),
                        baseHeightPx.toInt()
                    )
                }
            }
    )
}

private fun estimateElementBoundsPx(element: CanvasElement, density: Float, fontScale: Float): Pair<Float, Float> {
    return when (element) {
        is CanvasElement.ImageElement -> 120f * density to 120f * density
        is CanvasElement.StickerElement -> {
            val size = 44f * density * fontScale
            size to size
        }

        is CanvasElement.TextElement -> {
            val textWidth = (element.text.length.coerceAtLeast(1) * element.sizeSp * 0.58f) * density
            val textHeight = element.sizeSp * 1.35f * density
            textWidth to textHeight
        }
    }
}

private fun renderCanvasBitmap(
    elements: List<CanvasElement>,
    canvasWidthPx: Int,
    canvasHeightPx: Int,
    density: Float,
    scaledDensity: Float
): Bitmap {
    val bitmap = Bitmap.createBitmap(canvasWidthPx, canvasHeightPx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)

    val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        isSubpixelText = true
    }

    elements.forEach { element ->
        val t = element.transform
        canvas.save()
        canvas.translate(t.x, t.y)
        canvas.scale(t.scale, t.scale)
        canvas.rotate(t.rotation)

        when (element) {
            is CanvasElement.ImageElement -> {
                val targetSize = 120f * density
                val androidBitmap = element.bitmap.asAndroidBitmap().let { bmp ->
                    // Hardware bitmaps cannot be drawn onto a software Canvas — copy if needed
                    if (bmp.config == Bitmap.Config.HARDWARE) {
                        bmp.copy(Bitmap.Config.ARGB_8888, false)
                    } else {
                        bmp
                    }
                }
                canvas.drawBitmap(
                    androidBitmap,
                    null,
                    RectF(0f, 0f, targetSize, targetSize),
                    imagePaint
                )
            }

            is CanvasElement.TextElement -> {
                textPaint.color = element.color.toArgb()
                textPaint.textSize = element.sizeSp * scaledDensity
                textPaint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                val baseline = -textPaint.fontMetrics.ascent
                canvas.drawText(element.text, 0f, baseline, textPaint)
            }

            is CanvasElement.StickerElement -> {
                textPaint.color = android.graphics.Color.BLACK
                textPaint.textSize = 36f * scaledDensity
                textPaint.typeface = android.graphics.Typeface.DEFAULT
                val baseline = -textPaint.fontMetrics.ascent
                canvas.drawText(element.emoji, 0f, baseline, textPaint)
            }
        }

        canvas.restore()
    }

    return bitmap
}

private fun Uri.toImageBitmap(context: android.content.Context): ImageBitmap? = runCatching {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            // Force software allocation so this bitmap can be drawn on a software Canvas
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } else {
        context.contentResolver.openInputStream(this)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    }
    bitmap?.asImageBitmap()
}.getOrNull()
