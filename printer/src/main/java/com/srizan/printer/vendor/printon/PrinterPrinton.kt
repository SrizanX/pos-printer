package com.srizan.printer.vendor.printon

import android.content.Context
import android.graphics.Bitmap
import com.sr.SrPrinter
import com.srizan.printer.AbstractPrinter
import com.srizan.printer.config.BarcodeConfig
import com.srizan.printer.config.QRCodeConfig
import com.srizan.printer.config.TableConfig
import com.srizan.printer.config.TextConfig
import com.srizan.printer.enums.PrinterAlignment
import com.srizan.printer.enums.PrinterStatus

internal class PrinterPrinton(applicationContext: Context) : AbstractPrinter {
    private val printer: SrPrinter = SrPrinter.getInstance(applicationContext)

    override fun printText(text: String, config: TextConfig) {
        printer.run {
            setTextSize(config.size.toFloat())
            setAlignment(config.printerAlignment.ordinal)
            setTextBold(config.isBold)
            setTextItalic(config.isItalic)
            setTextUnderlined(config.isUnderLined)
            setTextInverseColor(config.isInverseColor)
            printText(text)
        }
    }

    override fun printTable(
        columns: Array<String>,
        tableConfig: TableConfig,
        textConfig: TextConfig
    ) {
        printer.printTableText(
            columns,
            tableConfig.weightArray,
            tableConfig.alignmentArray
        )
    }

    override fun printNewLine(lineCount: Int) {
        printer.nextLine(lineCount)
    }

    override fun printQRCode(data: String, qrCodeConfig: QRCodeConfig) {
        printer.run {
            setAlignment(qrCodeConfig.printerAlignment.ordinal)
            printQRCode(data, qrCodeConfig.size, qrCodeConfig.errorCorrectionLevel.ordinal)
        }
    }

    override fun printBarcode(
        data: String,
        barcodeConfig: BarcodeConfig
    ) {
        printer.run {
            setAlignment(barcodeConfig.printerAlignment.ordinal)
            printBarCode(
                data,
                barcodeConfig.symbology.ordinal,
                barcodeConfig.height,
                barcodeConfig.width
            )
        }
    }

    override fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment) {
        printer.run {
            setAlignment(printerAlignment.ordinal)
            printBitmap(bitmap)
        }
    }

    override fun getStatus(): PrinterStatus {
        return PrinterStatus.NORMAL
    }

    override fun getDeviceSerialNumber(): String {
        return ("No serial number")
    }
}

