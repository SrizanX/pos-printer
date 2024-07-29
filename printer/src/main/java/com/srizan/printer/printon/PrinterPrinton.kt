package com.srizan.printer.printon

import android.content.Context
import android.graphics.Bitmap
import com.sr.SrPrinter
import com.srizan.printer.AbstractPrinter
import com.srizan.printer.Alignment
import com.srizan.printer.BarcodeSymbology
import com.srizan.printer.BarcodeTextPosition
import com.srizan.printer.PrinterStatus
import com.srizan.printer.TableConfig
import com.srizan.printer.TextConfig
import com.srizan.printer.getIntAlignment

internal class PrinterPrinton(applicationContext: Context) : AbstractPrinter {
    private val printer: SrPrinter = SrPrinter.getInstance(applicationContext)

    override fun printText(text: String, config: TextConfig) {
        printer.run {
            setTextSize(config.size.toFloat())
            setAlignment(config.alignment.getIntAlignment())
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

    override fun printQRCode(data: String, size: Int, alignment: Alignment) {
        printer.setAlignment(alignment.getIntAlignment())
        printer.printQRCode(data, size, 3)
        printer.nextLine(3)
    }

    override fun printBarcode(
        data: String,
        height: Int,
        width: Int,
        alignment: Alignment,
        symbology: BarcodeSymbology,
        textPosition: BarcodeTextPosition
    ) {

    }

    override fun printImage(bitmap: Bitmap, alignment: Alignment) {
        printer.setAlignment(alignment.getIntAlignment())
        printer.printBitmap(bitmap)
    }

    override fun getStatus(): PrinterStatus {
        return PrinterStatus.NORMAL
    }
}

