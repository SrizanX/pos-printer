package com.srizan.printer

data class TextConfig(
    val size: Int = 24,
    val alignment: Alignment = Alignment.LEFT,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderLined: Boolean = false,
    val isStrikethrough: Boolean = false,
    val isInverseColor: Boolean = false
)


fun Alignment.getIntAlignment(): Int = when (this) {
    Alignment.LEFT -> 0
    Alignment.CENTER -> 1
    Alignment.RIGHT -> 2
}