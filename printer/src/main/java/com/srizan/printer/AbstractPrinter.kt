package com.srizan.printer

import android.graphics.Bitmap
import com.srizan.printer.config.BarcodeConfig
import com.srizan.printer.config.QRCodeConfig
import com.srizan.printer.config.TableConfig
import com.srizan.printer.config.TextConfig
import com.srizan.printer.enums.PrinterAlignment
import com.srizan.printer.enums.PrinterStatus

internal interface AbstractPrinter {
    fun printText(text: String, config: TextConfig)
    fun printTable(columns: Array<String>, tableConfig: TableConfig, textConfig: TextConfig)
    fun printNewLine(lineCount: Int)
    fun printQRCode(data: String, qrCodeConfig: QRCodeConfig)
    fun printBarcode(data: String, barcodeConfig: BarcodeConfig)
    fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment)
    fun getStatus(): PrinterStatus
    fun getDeviceSerialNumber(): String?
}