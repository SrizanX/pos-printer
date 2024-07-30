package com.srizan.printer.sunmi

import android.content.Context
import android.graphics.Bitmap
import android.os.RemoteException
import android.util.Log
import com.srizan.printer.AbstractPrinter
import com.srizan.printer.Alignment
import com.srizan.printer.BarcodeSymbology
import com.srizan.printer.BarcodeTextPosition
import com.srizan.printer.PrinterStatus
import com.srizan.printer.TableConfig
import com.srizan.printer.TextConfig
import com.srizan.printer.getIntAlignment
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
            Log.d("asd", "bindSunmiPrinterService: Success")
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
            Log.d("asd", "bindSunmiPrinterService: ${e.message}")
        }
    }

    override fun printText(text: String, config: TextConfig) {
        try {
            printer?.setAlignment(config.alignment.getIntAlignment(), null)
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
        printer?.printColumnsString(
            columns,
            tableConfig.weightArray,
            tableConfig.alignmentArray,
            null
        )
    }

    override fun printNewLine(lineCount: Int) {
        printer?.lineWrap(lineCount, null)
    }

    override fun printQRCode(data: String, size: Int, alignment: Alignment) {
        printer?.setAlignment(alignment.getIntAlignment(), null)
        printer?.printQRCode(data, size, 1, null)
        printer?.lineWrap(3, null)
    }

    override fun printBarcode(
        data: String,
        height: Int,
        width: Int,
        alignment: Alignment,
        symbology: BarcodeSymbology,
        textPosition: BarcodeTextPosition
    ) {
        if (printer == null) return
        try {
            printer?.printBarCode(
                data,
                symbology.ordinal,
                height,
                width,
                textPosition.ordinal,
                object : InnerResultCallback() {
                    override fun onRunResult(isSuccess: Boolean) {
                        Log.d("asd", "onRunResult: $isSuccess")
                    }

                    override fun onReturnString(result: String?) {
                        Log.d("asd", "onReturnString: $result")
                    }

                    override fun onRaiseException(code: Int, msg: String?) {
                        Log.d("asd", "onRaiseException: code ->$code, msg: $msg")
                    }

                    override fun onPrintResult(code: Int, msg: String?) {
                        Log.d("asd", "onPrintResult: code ->$code, msg: $msg")
                    }
                }
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printImage(bitmap: Bitmap, alignment: Alignment) {
        printer?.setAlignment(alignment.getIntAlignment(), null)
        printer?.printBitmap(bitmap, null)
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
}
