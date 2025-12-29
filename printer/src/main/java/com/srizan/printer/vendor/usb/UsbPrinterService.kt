package com.srizan.printer.vendor.usb

import android.app.PendingIntent
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import com.dantsu.escposprinter.EscPosCharsetEncoding
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import com.srizan.printer.core.AbstractPrinter
import com.srizan.printer.core.config.BarcodeConfig
import com.srizan.printer.core.config.QRCodeConfig
import com.srizan.printer.core.config.TableConfig
import com.srizan.printer.core.config.TextConfig
import com.srizan.printer.core.enums.PrinterAlignment
import com.srizan.printer.core.enums.PrinterStatus
import kotlin.properties.Delegates


class UsbPrinterService(private val context: Context) : AbstractPrinter {


    // Connection state enum
    enum class ConnectionState {
        DISCONNECTED,
        AWAITING_PERMISSION,
        CONNECTED,
        ERROR
    }

    // Observable property using Delegates.observable
    // Observable property using Delegates
    var connectionState by Delegates.observable(ConnectionState.DISCONNECTED) { _, oldState, newState ->
        if (oldState != newState) {
            when (newState) {
                ConnectionState.CONNECTED -> {
                    // Execute pending callback if available
                    connectionCallback?.invoke(true)
                    connectionCallback = null
                }

                ConnectionState.ERROR -> {
                    connectionCallback?.invoke(false)
                    connectionCallback = null
                    errorCallback?.invoke("USB printer permission denied or error occurred")
                    errorCallback = null
                }

                else -> {}
            }
        }
    }

    // Callback management
    private var connectionCallback: ((Boolean) -> Unit)? = null
    private var errorCallback: ((String) -> Unit)? = null
    private var activeDevice: UsbDevice? = null

    private var printer: EscPosPrinter? = null
    private val stringBuffer = StringBuffer()

    /**
     * Check if printer is ready and perform operation only if connected
     * @param onPermissionResult: Called with true if printer is ready, false otherwise
     * @param onError: Called with error message if printer error occurs
     */
    fun checkPrinterReadiness(
        onPermissionResult: (Boolean) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        errorCallback = onError
        val usbConnection = UsbPrintersConnections.selectFirstConnected(context)
        if (usbConnection == null) {
            onError("No USB printer found")
            return
        }

        // Set callback to be executed when permission is granted
        connectionCallback = onPermissionResult
        connectionState = ConnectionState.AWAITING_PERMISSION
        sendPrintPermissionBroadcast()
    }


    // Initiate usb printing by calling this method. It will trigger the broadcast receiver
    private fun sendPrintPermissionBroadcast() {
        val usbConnection = UsbPrintersConnections.selectFirstConnected(context)
            ?: return

        val permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_IMMUTABLE
            else 0
        )
        getUsbManager().requestPermission(usbConnection.device, permissionIntent)
    }

    fun handlePermissionResult(device: UsbDevice?, permissionGranted: Boolean) {
        if (permissionGranted && device != null) {
            activeDevice = device
            initializePrinter(device)
            connectionState = ConnectionState.CONNECTED
        } else {
            connectionState = ConnectionState.ERROR
            activeDevice = null
        }
    }

    private fun initializePrinter(device: UsbDevice) {
        try {
            printer = EscPosPrinter(
                UsbConnection(getUsbManager(), device),
                PRINTER_DPI,
                PRINTER_WIDTH_MM,
                PRINTER_NBR_CHARACTERS_PER_LINE,
                escPosCharsetEncoding
            )
        } catch (e: Exception) {
            connectionState = ConnectionState.ERROR
            errorCallback?.invoke("Failed to initialize printer: ${e.message}")
        }
    }

    fun sendPrintCommand() {
        if (printer == null) {
            errorCallback?.invoke("Printer is not initialized")
            connectionState = ConnectionState.ERROR
            return
        }
        try {
            printer?.printFormattedTextAndCut(stringBuffer.toString())
        } catch (e: Throwable) {
            errorCallback?.invoke("${e.message}")
        } finally {
            //Timber.tag("USB").d("USB Payload: \n$stringBuffer")
            clearText()
        }
    }


    fun appendText(text: String, textConfiguration: TextConfig) {
        val str = UsbTextConfigurationBuilder.getConfiguredText(text, textConfiguration)
        stringBuffer.append(str)
    }

    fun appendQrCode(
        text: String,
        size: Int,
        printerAlignment: PrinterAlignment = PrinterAlignment.CENTER
    ) {
        val usbPrinterAlignment = when (printerAlignment) {
            PrinterAlignment.LEFT -> "L"
            PrinterAlignment.CENTER -> "C"
            PrinterAlignment.RIGHT -> "R"
        }
        stringBuffer.append("[$usbPrinterAlignment]<qrcode size='$size'>$text</qrcode>\n")
    }

    private fun clearText() = stringBuffer.setLength(0)

    private fun getUsbManager() = context.getSystemService(USB_SERVICE) as UsbManager

    companion object {
        val escPosCharsetEncoding = EscPosCharsetEncoding("windows-1252", 16)
        const val PRINTER_DPI = 203
        const val PRINTER_WIDTH_MM = 72f
        const val PRINTER_NBR_CHARACTERS_PER_LINE = 42

        const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }

    override fun printText(text: String, config: TextConfig) {
        TODO("Not yet implemented")
    }

    override fun printTable(
        columns: Array<String>,
        tableConfig: TableConfig,
        textConfig: TextConfig
    ) {
        TODO("Not yet implemented")
    }

    override fun printNewLine(lineCount: Int) {
        TODO("Not yet implemented")
    }

    override fun printQRCode(data: String, qrCodeConfig: QRCodeConfig) {
        TODO("Not yet implemented")
    }

    override fun printBarcode(data: String, barcodeConfig: BarcodeConfig) {
        TODO("Not yet implemented")
    }

    override fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment) {
        TODO("Not yet implemented")
    }

    override fun getStatus(): PrinterStatus {
        TODO("Not yet implemented")
    }

    override fun getDeviceSerialNumber(): String? {
        TODO("Not yet implemented")
    }
}