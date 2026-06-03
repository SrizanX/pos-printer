package com.srizan.printy.feature.canvas

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

sealed class CanvasElement {
    abstract val id: String
    abstract val transform: Transform

    data class ImageElement(
        override val id: String,
        override val transform: Transform,
        val bitmap: ImageBitmap
    ) : CanvasElement()

    data class TextElement(
        override val id: String,
        override val transform: Transform,
        val text: String,
        val color: Color = Color.Black,
        val sizeSp: Float = 24f
    ) : CanvasElement()

    data class StickerElement(
        override val id: String,
        override val transform: Transform,
        val emoji: String
    ) : CanvasElement()
}

data class Transform(
    val x: Float,
    val y: Float,
    val scale: Float = 1f,
    val rotation: Float = 0f
)