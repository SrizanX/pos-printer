@file:Suppress("MemberVisibilityCanBePrivate")

package com.srizan.printer

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import com.srizan.printer.core.PrinterService
import com.srizan.printer.core.PrinterRegistry
import com.srizan.printer.core.config.BarcodeConfig
import com.srizan.printer.core.config.QRCodeConfig
import com.srizan.printer.core.config.TableConfig
import com.srizan.printer.core.config.TextConfig
import com.srizan.printer.core.enums.PrinterAlignment
import com.srizan.printer.core.enums.PrinterDevice
import com.srizan.printer.core.enums.PrinterStatus


const val printer_key = "printer"
val Context.prefs: SharedPreferences
    get() = this.getSharedPreferences("printer_pref", Context.MODE_PRIVATE)

object Printer {
    private var printer: PrinterService? = null
    private lateinit var applicationContext: Context
    var selectedPrinter: PrinterDevice = PrinterDevice.NONE
        private set

    /**
     * Initialize the printer system. Must be called before using any printer functions.
     * @param applicationContext The application context
     */
    fun initializePrinter(applicationContext: Context) {
        this.applicationContext = applicationContext

        val device = applicationContext.prefs.getString(printer_key, PrinterDevice.NONE.name)
        device?.let {
            try {
                selectPrinter(PrinterDevice.valueOf(it))
            } catch (e: IllegalArgumentException) {
                // Invalid printer device stored, reset to NONE
                selectPrinter(PrinterDevice.NONE)
            }
        }
    }

    /**
     * Select and initialize a printer device.
     * @param printerDevice The printer device to use
     * @return true if selection was successful, false otherwise
     */
    fun selectPrinter(printerDevice: PrinterDevice): Boolean {
        applicationContext.prefs.edit().putString(printer_key, printerDevice.name).apply()

        if (printerDevice == PrinterDevice.NONE) {
            clearPrinter()
            return true
        }

        val factory = PrinterRegistry.getFactory(printerDevice)
        return if (factory != null) {
            try {
                printer = factory.createPrinter(applicationContext)
                selectedPrinter = printerDevice
                true
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                clearPrinter()
                false
            }
        } else {
            clearPrinter()
            false
        }
    }

    /**
     * Get all available printer devices based on included vendor modules.
     * @return Set of available printer devices
     */
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
        data: String, barcodeConfig: BarcodeConfig
    ) {
        printer?.printBarcode(data, barcodeConfig)
    }

    fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment) {
        printer?.printImage(bitmap, printerAlignment)
    }

    fun isOperational() = getStatus() == PrinterStatus.NORMAL

    fun getStatus(): PrinterStatus {
        return printer?.getStatus() ?: PrinterStatus.UNINITIALIZED
    }

    fun getDeviceSerialNumber(): String? {
        return printer?.getDeviceSerialNumber()
    }

}


fun ifPrinterOperational(print: () -> Unit) {
    if (Printer.isOperational()) print()
}