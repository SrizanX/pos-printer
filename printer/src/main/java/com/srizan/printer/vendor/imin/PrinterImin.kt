package com.srizan.printer.vendor.imin

import android.content.Context
import android.graphics.Bitmap
import com.imin.printer.InitPrinterCallback
import com.imin.printer.PrinterHelper
import com.srizan.printer.AbstractPrinter
import com.srizan.printer.config.BarcodeConfig
import com.srizan.printer.config.QRCodeConfig
import com.srizan.printer.config.TableConfig
import com.srizan.printer.config.TextConfig
import com.srizan.printer.enums.PrinterAlignment
import com.srizan.printer.enums.PrinterStatus

internal class PrinterImin(applicationContext: Context) : AbstractPrinter {
    private var printer = PrinterHelper.getInstance()

    private var serialNo: String = ""

    init {
        printer.initPrinterService(applicationContext, object : InitPrinterCallback {
            override fun onConnected() {
                printer.getPrinterSerialNumber(IminCallback { serial ->
                    serial?.let { this@PrinterImin.serialNo = it }

                })
            }

            override fun onDisconnected() {}
        })
    }

    fun enterPrinterBuffer(isEnabled: Boolean) {
        printer.enterPrinterBuffer(isEnabled)
    }

    /**
     * @param speed Available speed: [30, 40, 50, 60, 80, 100]
     * */
    fun setPrinterSpeed(speed: Int) {
        printer.printerSpeed = speed
    }

    fun commitAndExitPrinterBuffer() {
        printer.commitPrinterBuffer()
        printer.exitPrinterBuffer(true)
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
                config.printerAlignment.ordinal,
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
    override fun printQRCode(data: String, qrCodeConfig: QRCodeConfig) {
        printer.run {
            setQrCodeSize(qrCodeConfig.size)
            setQrCodeErrorCorrectionLev(qrCodeConfig.errorCorrectionLevel.ordinal)
            printQrCodeWithAlign(data, qrCodeConfig.printerAlignment.ordinal, null)
        }
    }

    /**
     *
     * */
    override fun printBarcode(
        data: String,
        barcodeConfig: BarcodeConfig
    ) {
        printer.printBarCodeWithFull(
            data,
            barcodeConfig.symbology.ordinal,
            barcodeConfig.width,
            barcodeConfig.height,
            barcodeConfig.textPosition.ordinal,
            barcodeConfig.printerAlignment.ordinal,
            null
        )

    }

    override fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment) {
        printer.run {
            setCodeAlignment(printerAlignment.ordinal)
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
}

