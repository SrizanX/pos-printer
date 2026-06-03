package com.srizan.printy.feature.canvas

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class CanvasViewModel : ViewModel() {

    private val _state = MutableStateFlow(CanvasState())
    val state: StateFlow<CanvasState> = _state.asStateFlow()

    fun addImage(bitmap: ImageBitmap) {
        _state.update {
            val item = CanvasElement.ImageElement(
                id = UUID.randomUUID().toString(),
                transform = Transform(120f, 120f),
                bitmap = bitmap
            )
            it.copy(elements = it.elements + item, selectedId = item.id)
        }
    }

    fun addText(text: String) {
        if (text.isBlank()) return
        _state.update {
            val item = CanvasElement.TextElement(
                id = UUID.randomUUID().toString(),
                transform = Transform(120f, 120f),
                text = text.trim()
            )
            it.copy(elements = it.elements + item, selectedId = item.id)
        }
    }

    fun addSticker(emoji: String) {
        _state.update {
            val item = CanvasElement.StickerElement(
                id = UUID.randomUUID().toString(),
                transform = Transform(140f, 140f),
                emoji = emoji
            )
            it.copy(elements = it.elements + item, selectedId = item.id)
        }
    }

    fun select(id: String?) {
        _state.update { it.copy(selectedId = id) }
    }

    fun updateTransform(id: String, transform: Transform) {
        _state.update { current ->
            current.copy(
                elements = current.elements.map { element ->
                    if (element.id != id) return@map element
                    when (element) {
                        is CanvasElement.ImageElement -> element.copy(transform = transform)
                        is CanvasElement.TextElement -> element.copy(transform = transform)
                        is CanvasElement.StickerElement -> element.copy(transform = transform)
                    }
                }
            )
        }
    }

    fun applyTransformDelta(
        id: String,
        panX: Float,
        panY: Float,
        zoomDelta: Float,
        rotationDelta: Float,
        canvasWidthPx: Int,
        canvasHeightPx: Int,
        elementBaseWidthPx: Int,
        elementBaseHeightPx: Int
    ) {
        _state.update { current ->
            current.copy(
                elements = current.elements.map { element ->
                    if (element.id != id) return@map element
                    val nextScale = (element.transform.scale * zoomDelta).coerceIn(0.2f, 6f)
                    val safeBaseWidth = elementBaseWidthPx.coerceAtLeast(1)
                    val safeBaseHeight = elementBaseHeightPx.coerceAtLeast(1)
                    val scaledWidth = safeBaseWidth * nextScale
                    val scaledHeight = safeBaseHeight * nextScale
                    val maxX = (canvasWidthPx - scaledWidth).coerceAtLeast(0f)
                    val maxY = (canvasHeightPx - scaledHeight).coerceAtLeast(0f)
                    val next = element.transform.copy(
                        x = (element.transform.x + panX).coerceIn(0f, maxX),
                        y = (element.transform.y + panY).coerceIn(0f, maxY),
                        scale = nextScale,
                        rotation = element.transform.rotation + rotationDelta
                    )
                    when (element) {
                        is CanvasElement.ImageElement -> element.copy(transform = next)
                        is CanvasElement.TextElement -> element.copy(transform = next)
                        is CanvasElement.StickerElement -> element.copy(transform = next)
                    }
                }
            )
        }
    }

    fun deleteSelected() {
        val selectedId = _state.value.selectedId ?: return
        _state.update {
            it.copy(elements = it.elements.filterNot { element -> element.id == selectedId }, selectedId = null)
        }
    }

    fun setPaperWidth(width: PaperWidth) {
        _state.update { it.copy(paperWidth = width) }
    }
}