package com.srizan.printer

import android.graphics.Bitmap

internal interface AbstractPrinter {
    fun printText(text: String, config: TextConfig)
    fun printTable(columns: Array<String>, tableConfig: TableConfig, textConfig: TextConfig)
    fun printNewLine(lineCount: Int)
    fun printQRCode(data: String, size: Int, alignment: Alignment)

    fun printBarcode(
        data: String,
        height: Int,
        width: Int,
        alignment: Alignment,
        symbology: BarcodeSymbology,
        textPosition: BarcodeTextPosition
    )

    fun printImage(bitmap: Bitmap, alignment: Alignment)
    fun getStatus(): PrinterStatus
    fun getDeviceSerialNumber(): String?
}