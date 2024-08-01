package com.srizan.printer.vendor.sunmi

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.RemoteException
import android.util.Log
import com.srizan.printer.AbstractPrinter
import com.srizan.printer.config.BarcodeConfig
import com.srizan.printer.config.QRCodeConfig
import com.srizan.printer.config.TableConfig
import com.srizan.printer.config.TextConfig
import com.srizan.printer.enums.PrinterAlignment
import com.srizan.printer.enums.PrinterStatus
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.InnerResultCallback
import com.sunmi.peripheral.printer.SunmiPrinterService
import com.sunmi.peripheral.printer.WoyouConsts

internal class PrinterSunmi(private val applicationContext: Context) : AbstractPrinter {

    var printer: SunmiPrinterService? = null

    private val innerPrinterCallback: InnerPrinterCallback = object : InnerPrinterCallback() {
        override fun onConnected(service: SunmiPrinterService) {
            printer = service
        }

        override fun onDisconnected() {
            printer = null
        }
    }

    init {
        bindSunmiPrinterService()
    }

    private fun bindSunmiPrinterService() {
        try {
            InnerPrinterManager.getInstance().bindService(
                applicationContext,
                innerPrinterCallback
            )
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    override fun printText(text: String, config: TextConfig) {
        try {
            printer?.setAlignment(config.printerAlignment.ordinal, null)
            printer?.setTextStyle(config)
            printer?.printTextWithFont(text, null, config.size.toFloat(), null)
        } catch (e: Exception) {
            Log.d("asd", "printText: ${e.localizedMessage}")
        }

    }

    override fun printTable(
        columns: Array<String>,
        tableConfig: TableConfig,
        textConfig: TextConfig
    ) {
        try {
            printer?.printColumnsString(
                columns,
                tableConfig.weightArray,
                tableConfig.alignmentArray,
                null
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printNewLine(lineCount: Int) {
        try {
            printer?.lineWrap(lineCount, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printQRCode(data: String, qrCodeConfig: QRCodeConfig) {
        try {
            printer?.setAlignment(qrCodeConfig.printerAlignment.ordinal, null)
            printer?.printQRCode(
                data,
                qrCodeConfig.size,
                qrCodeConfig.errorCorrectionLevel.ordinal,
                null
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printBarcode(
        data: String,
        barcodeConfig: BarcodeConfig
    ) {
        try {
            printer?.printBarCode(
                data,
                barcodeConfig.symbology.ordinal,
                barcodeConfig.height,
                barcodeConfig.width,
                barcodeConfig.textPosition.ordinal,
                null
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment) {
        try {
            printer?.setAlignment(printerAlignment.ordinal, null)
            printer?.printBitmap(bitmap, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun getStatus(): PrinterStatus {
        if (printer == null) return PrinterStatus.DISCONNECTED
        return when (printer?.updatePrinterState()) {
            1 -> PrinterStatus.NORMAL
            4 -> PrinterStatus.OUT_OF_PAPER
            5 -> PrinterStatus.OVERHEATED
            6 -> PrinterStatus.OPEN_COVER
            else -> PrinterStatus.UNKNOWN
        }
    }

    private fun SunmiPrinterService.setTextStyle(config: TextConfig) {
        setPrinterStyle(
            WoyouConsts.ENABLE_BOLD,
            getStyleState(config.isBold)
        )

        setPrinterStyle(
            WoyouConsts.ENABLE_ILALIC,
            getStyleState(config.isItalic)
        )

        setPrinterStyle(
            WoyouConsts.ENABLE_UNDERLINE,
            getStyleState(config.isUnderLined)
        )

        setPrinterStyle(
            WoyouConsts.ENABLE_STRIKETHROUGH,
            getStyleState(config.isStrikethrough)
        )

        setPrinterStyle(
            WoyouConsts.ENABLE_ANTI_WHITE,
            getStyleState(config.isInverseColor)
        )
    }

    private fun getStyleState(isEnable: Boolean): Int {
        return if (isEnable) WoyouConsts.ENABLE else WoyouConsts.DISABLE
    }

    override fun getDeviceSerialNumber(): String? {
        val classObj = Class.forName("android.os.SystemProperties")
        val method = classObj.getMethod("get", String::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                method.invoke(classObj, "ro.sunmi.serial") as String
            } catch (e: Exception) {
                "ERROR_SERIAL_NOT_FOUND"
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Build.getSerial()
        } else {
            try {
                method.invoke(classObj, "ro.serialno") as String
            } catch (e: Exception) {
                "ERROR_SERIAL_NOT_FOUND"
            }
        }
    }
}
