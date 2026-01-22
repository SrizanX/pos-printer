package com.srizan.printer.core.config

import com.srizan.printer.core.enums.PrinterAlignment
import com.srizan.printer.core.enums.QRCodeErrorCorrectionLevel

data class QRCodeConfig @JvmOverloads constructor(
    val size: Int = 8,
    val printerAlignment: PrinterAlignment = PrinterAlignment.CENTER,
    val errorCorrectionLevel: QRCodeErrorCorrectionLevel = QRCodeErrorCorrectionLevel.M
)