@file:Suppress("MemberVisibilityCanBePrivate")

package com.srizan.printer

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.widget.Toast
import com.srizan.printer.core.AbstractPrinter
import com.srizan.printer.core.PrinterRegistry
import com.srizan.printer.core.config.BarcodeConfig
import com.srizan.printer.core.config.QRCodeConfig
import com.srizan.printer.core.config.TableConfig
import com.srizan.printer.core.config.TextConfig
import com.srizan.printer.core.enums.BarcodeSymbology
import com.srizan.printer.core.enums.PrinterAlignment
import com.srizan.printer.core.enums.PrinterDevice
import com.srizan.printer.core.enums.PrinterStatus


const val printer_key = "printer"
val Context.prefs: SharedPreferences
    get() = this.getSharedPreferences("printer_pref", Context.MODE_PRIVATE)

object Printer {
    private var printer: AbstractPrinter? = null
    private lateinit var applicationContext: Context
    var selectedPrinter: PrinterDevice = PrinterDevice.NONE

    fun initializePrinter(
        applicationContext: Context,
    ) {
        this.applicationContext = applicationContext

        val device = applicationContext.prefs.getString(printer_key, PrinterDevice.NONE.name)
        device?.let { selectPrinter(PrinterDevice.valueOf(it)) }
    }

    fun selectPrinter(printerDevice: PrinterDevice) {
        applicationContext.prefs.edit().putString(printer_key, printerDevice.name).apply()
        
        if (printerDevice == PrinterDevice.NONE) {
            clearPrinter()
            return
        }
        
        val factory = PrinterRegistry.getFactory(printerDevice)
        if (factory != null) {
            try {
                printer = factory.createPrinter(applicationContext)
                selectedPrinter = printerDevice
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                clearPrinter()
            }
        } else {
            clearPrinter()
        }
    }
    
    fun getAvailablePrinters(): Set<PrinterDevice> {
        return PrinterRegistry.getAvailableDevices()
    }

    private fun clearPrinter() {
        printer = null
        selectedPrinter = PrinterDevice.NONE
    }

    fun printText(text: String, config: TextConfig) {
        printer?.printText(text, config)
    }

    fun printTable(columns: Array<String>, tableConfig: TableConfig, textConfig: TextConfig) {
        printer?.printTable(columns, tableConfig, textConfig)
    }

    fun printNewLine(lineCount: Int) {
        printer?.printNewLine(lineCount)
    }

    fun printQRCode(data: String, qrCodeConfig: QRCodeConfig) {
        printer?.printQRCode(data, qrCodeConfig)
    }

    fun printBarcode(
        data: String,
        barcodeConfig: BarcodeConfig
    ) {
        printer?.printBarcode(data, barcodeConfig)
    }

    fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment) {
        printer?.printImage(bitmap, printerAlignment)
    }

    fun isOperational() = getStatus() == PrinterStatus.NORMAL

    fun getStatus(): PrinterStatus {
        val status = printer?.getStatus() ?: PrinterStatus.UNINITIALIZED
        when (status) {
            PrinterStatus.NORMAL -> {}
            PrinterStatus.DISCONNECTED -> applicationContext.showToastMessage(R.string.printer_status_disconnected)
            PrinterStatus.UNKNOWN -> applicationContext.showToastMessage(R.string.printer_status_error_unknown)
            PrinterStatus.OVERHEATED -> applicationContext.showToastMessage(R.string.printer_status_overheating)
            PrinterStatus.OUT_OF_PAPER -> applicationContext.showToastMessage(R.string.printer_status_out_of_paper)
            PrinterStatus.OPEN_COVER -> applicationContext.showToastMessage(R.string.printer_status_cover_is_not_closed)
            PrinterStatus.UNINITIALIZED -> applicationContext.showToastMessage(R.string.printer_status_uninitialized)
        }
        return status
    }

    fun getDeviceSerialNumber(): String? {
        return printer?.getDeviceSerialNumber()
    }

    fun test(logo: Bitmap?) {
        val defaultTextConfig = TextConfig()
        val labelTextConfig =
            TextConfig(size = 26, printerAlignment = PrinterAlignment.CENTER, isBold = true)

        printText("Bengali\n", labelTextConfig)
        printText("যাত্রী সার্ভিসেস লিমিটেড\n", defaultTextConfig)

        printText("\nAlignment\n", labelTextConfig)
        printText(
            "যাত্রী সার্ভিসেস লিমিটেড\n",
            defaultTextConfig.copy(printerAlignment = PrinterAlignment.LEFT)
        )
        printText(
            "যাত্রী সার্ভিসেস লিমিটেড\n",
            defaultTextConfig.copy(printerAlignment = PrinterAlignment.CENTER)
        )
        printText(
            "যাত্রী সার্ভিসেস লিমিটেড\n",
            defaultTextConfig.copy(printerAlignment = PrinterAlignment.RIGHT)
        )


        printText("\nFont Sizes\n", labelTextConfig)
        (24..40 step 1).forEach { n ->
            printText("যাত্রী সার্ভিসেস লিমিটেড - $n\n", defaultTextConfig.copy(size = n))
        }


        printText("\nTable Print: 2 Columns\n", labelTextConfig)
        val tableConfig2Col = TableConfig(
            weightArray = intArrayOf(1, 1),
            alignmentArray = intArrayOf(0, 2),
            sizeArray = intArrayOf(24, 24)
        )
        printTable(arrayOf("Item", "Price"), tableConfig2Col, defaultTextConfig)
        printTable(arrayOf("ক", "৳১০০"), tableConfig2Col, defaultTextConfig)
        printTable(arrayOf("খ", "৳২০০"), tableConfig2Col, defaultTextConfig)
        printTable(arrayOf("গ", "৳৩০০"), tableConfig2Col, defaultTextConfig)
        printTable(arrayOf("ঘ", "৳৪০০"), tableConfig2Col, defaultTextConfig)
        printTable(arrayOf("ঙ", "৳৫০০"), tableConfig2Col, defaultTextConfig)

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
        printNewLine(1)


        logo?.let {
            printText("\nImage\n", labelTextConfig)
            printImage(logo, PrinterAlignment.CENTER)
            printNewLine(1)
        }

        printText("\nQR Code\n", labelTextConfig)
        printQRCode("Jatri Services Ltd.", QRCodeConfig())
        printNewLine(3)

        printText("\nBarcode\n", labelTextConfig)
        printBarcode("123456789012", BarcodeConfig(symbology = BarcodeSymbology.CODE_128))
        printNewLine(3)
    }

}


fun Context.showToastMessage(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun ifPrinterOperational(print: () -> Unit) {
    if (Printer.isOperational()) print()
}