package com.srizan.printer.core.config

import com.srizan.printer.core.enums.BarcodeSymbology
import com.srizan.printer.core.enums.BarcodeTextPosition
import com.srizan.printer.core.enums.PrinterAlignment

data class BarcodeConfig @JvmOverloads constructor(
    val symbology: BarcodeSymbology,
    val height: Int = 162,
    val width: Int = 2,
    val printerAlignment: PrinterAlignment = PrinterAlignment.CENTER,
    val textPosition: BarcodeTextPosition = BarcodeTextPosition.BOTTOM
)