@file:Suppress("MemberVisibilityCanBePrivate")

package com.srizan.printer

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.srizan.printer.imin.PrinterImin
import com.srizan.printer.nexgo.PrinterNexgo
import com.srizan.printer.printon.PrinterPrinton
import com.srizan.printer.sunmi.PrinterSunmi

object Printer {
    private lateinit var printer: AbstractPrinter
    private lateinit var applicationContext: Context
    lateinit var selectedPrinter: PrinterDevice

    fun initializePrinter(
        applicationContext: Context,
    ) {
        this.applicationContext = applicationContext
    }

    fun selectPrinter(printerDevice: PrinterDevice) {
        when (printerDevice) {
            PrinterDevice.SUNMI -> {
                printer = PrinterSunmi(applicationContext)
                selectedPrinter = printerDevice
            }

            PrinterDevice.PRINTON -> {
                printer = PrinterPrinton(applicationContext)
                selectedPrinter = printerDevice
            }

            PrinterDevice.IMIN -> {
                printer = PrinterImin(applicationContext)
                selectedPrinter = printerDevice
            }

            PrinterDevice.NEXGO -> {
                try {
                    printer = PrinterNexgo(applicationContext)
                    selectedPrinter = printerDevice
                } catch (thr: Throwable) {
                    thr.printStackTrace()
                }
            }
        }
    }

    fun printText(text: String, config: TextConfig) {
        printer.printText(text, config)
    }

    fun printTable(columns: Array<String>, tableConfig: TableConfig, textConfig: TextConfig) {
        printer.printTable(columns, tableConfig, textConfig)
    }

    fun printNewLine(lineCount: Int) {
        printer.printNewLine(lineCount)
    }

    fun printQRCode(data: String, size: Int = 6, alignment: Alignment = Alignment.CENTER) {
        printer.printQRCode(data, size, alignment)
    }

    fun printBarcode(
        data: String,
        height: Int,
        width: Int,
        alignment: Alignment,
        symbology: BarcodeSymbology,
        textPosition: BarcodeTextPosition
    ) {
        printer.printBarcode(data, height, width, alignment, symbology, textPosition)
    }

    fun printImage(bitmap: Bitmap, alignment: Alignment) {
        printer.printImage(bitmap, alignment)
    }

    fun isOperational() = getStatus() == PrinterStatus.NORMAL

    fun getStatus(): PrinterStatus {
        val status = printer.getStatus()
        when (status) {
            PrinterStatus.NORMAL -> {}
            PrinterStatus.DISCONNECTED -> applicationContext.showToastMessage(R.string.printer_status_disconnected)
            PrinterStatus.UNKNOWN -> applicationContext.showToastMessage(R.string.printer_status_error_unknown)
            PrinterStatus.OVERHEATED -> applicationContext.showToastMessage(R.string.printer_status_overheating)
            PrinterStatus.OUT_OF_PAPER -> applicationContext.showToastMessage(R.string.printer_status_out_of_paper)
            PrinterStatus.OPEN_COVER -> applicationContext.showToastMessage(R.string.printer_status_cover_is_not_closed)
        }
        return printer.getStatus()
    }

    fun getDeviceSerialNumber(): String? {
        return printer.getDeviceSerialNumber()
    }

    fun test(logo: Bitmap?) {
        val defaultTextConfig = TextConfig()
        val labelTextConfig = TextConfig(size = 26, alignment = Alignment.CENTER, isBold = true)

        printText("Bengali\n", labelTextConfig)
        printText("যাত্রী সার্ভিসেস লিমিটেড\n", defaultTextConfig)

        printText("\nAlignment\n", labelTextConfig)
        printText("Left\n", defaultTextConfig.copy(alignment = Alignment.LEFT))
        printText("Center\n", defaultTextConfig.copy(alignment = Alignment.CENTER))
        printText("Right\n", defaultTextConfig.copy(alignment = Alignment.RIGHT))


        printText("\nFont Sizes\n", labelTextConfig)
        (24..40 step 2).forEach { n ->
            printText("Font SIze: $n\n", defaultTextConfig.copy(size = n))
        }


        printText("\nTable Print: 2 Columns\n", labelTextConfig)
        val tableConfig2Col = TableConfig(
            weightArray = intArrayOf(1, 1),
            alignmentArray = intArrayOf(0, 2),
            sizeArray = intArrayOf(24, 24)
        )
        printTable(arrayOf("Item", "Price"), tableConfig2Col, defaultTextConfig)
        printTable(arrayOf("A", "$1"), tableConfig2Col, defaultTextConfig)
        printTable(arrayOf("B", "$2"), tableConfig2Col, defaultTextConfig)
        printTable(arrayOf("C", "$3"), tableConfig2Col, defaultTextConfig)

        printText("\nTable Print: 3 Columns\n", labelTextConfig)

        val tableConfig3Col = TableConfig(
            weightArray = intArrayOf(1, 1, 1),
            alignmentArray = intArrayOf(0, 1, 2),
            sizeArray = intArrayOf(24, 24, 24)
        )


        printTable(
            arrayOf("Item", "Qty", "Price"), tableConfig3Col, defaultTextConfig
        )
        printTable(
            arrayOf("A", "1", "$10"), tableConfig3Col, defaultTextConfig
        )
        printTable(
            arrayOf("B", "2", "$20"), tableConfig3Col, defaultTextConfig
        )
        printTable(
            arrayOf("C", "3", "$30"), tableConfig3Col, defaultTextConfig
        )


        logo?.let {
            printText("\nImage\n", labelTextConfig)
            printImage(logo, Alignment.CENTER)
        }

        printText("\nQR Code\n", labelTextConfig)
        printQRCode("Jatri Services Ltd.", 8)

        if (printer is PrinterNexgo) {
            (printer as PrinterNexgo).startPrint()
        }
    }

}


fun Context.showToastMessage(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun log(msg: String, tag: String = "asd") {
    if (BuildConfig.DEBUG) Log.d(tag, msg)
}

fun ifPrinterOperational(block: () -> Unit) {
    if (Printer.isOperational()) block()
}