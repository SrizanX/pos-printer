package com.srizan.printer.config

import com.srizan.printer.enums.PrinterAlignment

data class TextConfig(
    val size: Int = 24,
    val printerAlignment: PrinterAlignment = PrinterAlignment.LEFT,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderLined: Boolean = false,
    val isStrikethrough: Boolean = false,
    val isInverseColor: Boolean = false
)