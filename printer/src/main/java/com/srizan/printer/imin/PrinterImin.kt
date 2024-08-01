package com.srizan.printer.imin

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.imin.printer.PrinterHelper
import com.srizan.printer.AbstractPrinter
import com.srizan.printer.Alignment
import com.srizan.printer.BarcodeSymbology
import com.srizan.printer.BarcodeTextPosition
import com.srizan.printer.PrinterStatus
import com.srizan.printer.TableConfig
import com.srizan.printer.TextConfig
import com.srizan.printer.getIntAlignment
import com.srizan.printer.log

internal class PrinterImin(applicationContext: Context) : AbstractPrinter {
    private var printer = PrinterHelper.getInstance()

    private var serialNo: String = ""

    init {
        printer.initPrinterService(applicationContext)
        printer.getPrinterSerialNumber(IminCallback { serial ->
            serial?.let { this.serialNo = it }
            log("brd sn No: $serial")
        })
        log("Imin printer initialized")
    }

    override fun printText(text: String, config: TextConfig) {
        printer.run {
            setTextBitmapSize(config.size)
            setTextBitmapStyle(config.getTextStyleImin())
            setTextBitmapUnderline(config.isUnderLined)
            setTextBitmapStrikeThru(config.isStrikethrough)
            setTextBitmapAntiWhite(config.isInverseColor)
            printTextBitmapWithAli(
                text.removeSuffix("\n"),
                config.alignment.getIntAlignment(),
                null
            )
        }
    }

    override fun printTable(
        columns: Array<String>,
        tableConfig: TableConfig,
        textConfig: TextConfig
    ) {
        printer.printColumnsString(
            columns,
            tableConfig.weightArray,
            tableConfig.alignmentArray,
            tableConfig.sizeArray,
            null
        )
    }

    override fun printNewLine(lineCount: Int) {
        repeat(lineCount) {
            printer.printAndLineFeed()
        }
    }

    /**
     * @param size 1 <= size <= 11
     * */
    override fun printQRCode(data: String, size: Int, alignment: Alignment) {
        printer.run {
            setQrCodeSize(if (size > 11) 11 else size)
            printQrCodeWithAlign(data, alignment.getIntAlignment(), null)
            printNewLine(3)
        }
    }

    /**
     *
     * */
    override fun printBarcode(
        data: String,
        height: Int,
        width: Int,
        alignment: Alignment,
        symbology: BarcodeSymbology,
        textPosition: BarcodeTextPosition
    ) {
        printer.printBarCodeWithFull(
            data,
            symbology.ordinal,
            width,
            height,
            textPosition.ordinal,
            alignment.getIntAlignment(),
            null
        )

    }

    override fun printImage(bitmap: Bitmap, alignment: Alignment) {
        printer.run {
            setCodeAlignment(alignment.getIntAlignment())
            printBitmap(bitmap, null)
        }
    }

    /**
     * Normal: 0
     * Printer cover open: 3
     * Out of paper: 7
     * */
    override fun getStatus(): PrinterStatus {
        return when (printer.printerStatus) {
            -1 -> PrinterStatus.DISCONNECTED
            3 -> PrinterStatus.OPEN_COVER
            4 -> PrinterStatus.OVERHEATED
            7 -> PrinterStatus.OUT_OF_PAPER
            0 -> PrinterStatus.NORMAL
            else -> PrinterStatus.UNKNOWN
        }
    }

    override fun getDeviceSerialNumber(): String {
        return this.serialNo
    }

    private fun serialNo() {
        printer.getPrinterSerialNumber(IminCallback { seial ->
            Log.d("asd", "brd sn No: $seial")

        }
        )

        printer.getPrinterModelName(IminCallback { model ->
            Log.d("asd", "Model No: $model")
        })


        printer.getPrinterThermalHead(IminCallback { model ->
            Log.d("asd", "TH Model No: $model")
        })

    }
}

