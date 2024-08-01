package com.srizan.printer.vendor.nexgo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import com.nexgo.oaf.apiv3.APIProxy
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.SdkResult
import com.nexgo.oaf.apiv3.device.printer.LineOptionEntity
import com.nexgo.oaf.apiv3.device.printer.Printer
import com.srizan.printer.AbstractPrinter
import com.srizan.printer.config.BarcodeConfig
import com.srizan.printer.config.QRCodeConfig
import com.srizan.printer.config.TableConfig
import com.srizan.printer.config.TextConfig
import com.srizan.printer.enums.PrinterAlignment
import com.srizan.printer.enums.PrinterStatus

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
            config.printerAlignment.getNexgoAlignEnum(),
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

    override fun printQRCode(data: String, qrCodeConfig: QRCodeConfig) {
        printer?.appendQRcode(
            data,
            qrCodeConfig.size.times(25),
            qrCodeConfig.printerAlignment.getNexgoAlignEnum()
        )
    }

    override fun printBarcode(
        data: String,
        barcodeConfig: BarcodeConfig
    ) {

        printer?.appendBarcode(
            data,
            barcodeConfig.height,
            0,//margin
            barcodeConfig.width,
            barcodeConfig.symbology.getNexgoSymbology(),
            barcodeConfig.printerAlignment.getNexgoAlignEnum()
        )
        startPrint()

    }

    override fun printImage(bitmap: Bitmap, printerAlignment: PrinterAlignment) {
        printer?.appendImage(bitmap, printerAlignment.getNexgoAlignEnum())
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
        printer?.startPrint(true) { n -> }
    }
}