package com.srizan.printer.core

import android.graphics.Bitmap
import com.srizan.printer.core.config.BarcodeConfig
import com.srizan.printer.core.config.QRCodeConfig
import com.srizan.printer.core.config.TableConfig
import com.srizan.printer.core.config.TextConfig
import com.srizan.printer.core.enums.PrinterAlignment
import com.srizan.printer.core.enums.PrinterStatus

interface AbstractPrinter {
    fun printText(text: String, config: TextConfig)
    fun printTable(columns: Array<String>, tableConfig: TableConfig, textConfig: TextConfig)
    fun printNewLine(lineCount: Int)
    fun printQRCode(data: String, qrCodeConfig: QRCodeConfig)
    fun printBarcode(data: String, barcodeConfig: BarcodeConfig)
    fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment)
    fun getStatus(): PrinterStatus
    fun getDeviceSerialNumber(): String?
}