package com.srizan.printer.config

import com.srizan.printer.enums.PrinterAlignment
import com.srizan.printer.enums.QRCodeErrorCorrectionLevel

data class QRCodeConfig(
    val size: Int = 8,
    val printerAlignment: PrinterAlignment = PrinterAlignment.CENTER,
    val errorCorrectionLevel: QRCodeErrorCorrectionLevel = QRCodeErrorCorrectionLevel.M
)