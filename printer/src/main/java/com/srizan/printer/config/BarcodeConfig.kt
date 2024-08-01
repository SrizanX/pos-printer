package com.srizan.printer.config

import com.srizan.printer.enums.PrinterAlignment
import com.srizan.printer.enums.BarcodeSymbology
import com.srizan.printer.enums.BarcodeTextPosition

data class BarcodeConfig(
    val height: Int = 162,
    val width: Int = 2,
    val printerAlignment: PrinterAlignment = PrinterAlignment.CENTER,
    val symbology: BarcodeSymbology,
    val textPosition: BarcodeTextPosition = BarcodeTextPosition.BOTTOM
)