package com.srizan.util.ditherer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.graphics.scale

class Ditherer(
    private val kernel: DitherKernel = DitherPresets.atkinson,
    private val targetWidth: Int = 384,
    private val threshold: Int = 128
) {
    fun dither(context: Context, uri: Uri): Bitmap {
        val original = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        return dither(original).also { original.recycle() }
    }

    fun dither(source: Bitmap): Bitmap {
        val aspectRatio = source.height.toFloat() / source.width.toFloat()
        val targetHeight = (targetWidth * aspectRatio + 0.5f).toInt()

        val resized = source.scale(targetWidth, targetHeight)

        val width = resized.width
        val height = resized.height
        val pixels = IntArray(width * height)

        resized.getPixels(pixels, 0, width, 0, 0, width, height)
        resized.recycle()

        // Convert to grayscale, blending onto white to handle transparency
        val gray = FloatArray(width * height)
        for (i in pixels.indices) {
            val color = pixels[i]
            val alpha = (color ushr 24) and 0xFF
            val invAlpha = 255 - alpha
            val r = ((color shr 16) and 0xFF) * alpha / 255 + invAlpha
            val g = ((color shr 8)  and 0xFF) * alpha / 255 + invAlpha
            val b = ( color         and 0xFF) * alpha / 255 + invAlpha
            gray[i] = 0.299f * r + 0.587f * g + 0.114f * b
        }

        val outputPixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                val oldPixel = gray[index].coerceIn(0f, 255f)

                val bwInt = if (oldPixel >= threshold) 255 else 0
                outputPixels[index] = (0xFF shl 24) or (bwInt shl 16) or (bwInt shl 8) or bwInt

                val rawError = oldPixel - bwInt.toFloat()

                for (offset in kernel.distribution) {
                    val nx = x + offset.dx
                    val ny = y + offset.dy
                    if (nx in 0 until width && ny in 0 until height) {
                        gray[ny * width + nx] += (rawError * offset.weight) / kernel.divisor
                    }
                }
            }
        }

        return Bitmap.createBitmap(outputPixels, width, height, Bitmap.Config.ARGB_8888)
    }
}