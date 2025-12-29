package com.srizan.printer.imin

import android.content.Context
import com.srizan.printer.core.AbstractPrinter
import com.srizan.printer.core.PrinterFactory
import com.srizan.printer.core.PrinterRegistry
import com.srizan.printer.core.enums.PrinterDevice

class IminPrinterFactory : PrinterFactory {
    override fun getSupportedDevice() = PrinterDevice.IMIN

    override fun createPrinter(context: Context): AbstractPrinter {
        return PrinterImin(context)
    }

    companion object {
        @JvmStatic
        fun register() {
            PrinterRegistry.registerFactory(IminPrinterFactory())
        }
    }
}
