package com.srizan.printer.nexgo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.util.Log
import com.nexgo.oaf.apiv3.APIProxy
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.SdkResult
import com.nexgo.oaf.apiv3.device.printer.AlignEnum
import com.nexgo.oaf.apiv3.device.printer.BarcodeFormatEnum
import com.nexgo.oaf.apiv3.device.printer.LineOptionEntity
import com.nexgo.oaf.apiv3.device.printer.Printer
import com.sr.SrPrinter
import com.srizan.printer.AbstractPrinter
import com.srizan.printer.Alignment
import com.srizan.printer.BarcodeSymbology
import com.srizan.printer.BarcodeTextPosition
import com.srizan.printer.PrinterStatus
import com.srizan.printer.TableConfig
import com.srizan.printer.TextConfig

internal class PrinterNexgo(applicationContext: Context) : AbstractPrinter {

    private val deviceEngine: DeviceEngine = APIProxy.getDeviceEngine(applicationContext)
    private var printer: Printer? = null

    init {
        try {
            printer = deviceEngine.printer
            printer?.initPrinter()
            printer?.setTypeface(Typeface.DEFAULT)

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            throw throwable
        }
    }

    override fun printText(text: String, config: TextConfig) {
        val lineOptionEntity = LineOptionEntity.Builder()
            .setBold(config.isBold)
            .setUnderline(config.isUnderLined)
            .build()
        printer?.appendPrnStr(
            text,
            config.size,
            config.alignment.getNexgoAlignEnum(),
            lineOptionEntity
        )
    }

    override fun printTable(
        columns: Array<String>,
        tableConfig: TableConfig,
        textConfig: TextConfig
    ) {
        if (columns.size == 2) {
            val lineOptionEntity = LineOptionEntity.Builder()
                .setBold(textConfig.isBold)
                .setUnderline(textConfig.isUnderLined)
                .build()
            printer?.appendPrnStr(columns[0], columns[1], textConfig.size, lineOptionEntity)
        }
    }

    override fun printNewLine(lineCount: Int) {
        printer?.setLetterSpacing(lineCount)
    }

    override fun printQRCode(data: String, size: Int, alignment: Alignment) {
        printer?.appendQRcode(data, size.times(25), alignment.getNexgoAlignEnum())
    }

    override fun printBarcode(
        data: String,
        height: Int,
        width: Int,
        alignment: Alignment,
        symbology: BarcodeSymbology,
        textPosition: BarcodeTextPosition
    ) {

        printer?.appendBarcode(
            data,
            height,
            0,
            width,
            symbology.getNexgoSymbology(),
            alignment.getNexgoAlignEnum()
        )
        startPrint()

    }

    override fun printImage(bitmap: Bitmap, alignment: Alignment) {
        printer?.appendImage(bitmap, alignment.getNexgoAlignEnum())
    }

    override fun getStatus(): PrinterStatus {
        when (printer?.status) {
            SdkResult.Printer_PaperLack -> return PrinterStatus.OUT_OF_PAPER
            SdkResult.Printer_TooHot -> return PrinterStatus.OVERHEATED
        }
        return PrinterStatus.NORMAL
    }

    override fun getDeviceSerialNumber(): String? {
        return deviceEngine.deviceInfo.sn
    }

    fun startPrint() {
        printer?.startPrint(true) { n ->
            Log.d("asd", "startPrint: $n")
        }
    }
}