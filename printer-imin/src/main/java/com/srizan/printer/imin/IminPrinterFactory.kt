package com.srizan.printer.imin

import android.content.Context
import com.srizan.printer.core.PrinterService
import com.srizan.printer.core.PrinterFactory
import com.srizan.printer.core.PrinterRegistry
import com.srizan.printer.core.enums.PrinterDevice

class IminPrinterFactory : PrinterFactory {
    override fun getSupportedDevice() = PrinterDevice.IMIN

    override fun createPrinter(context: Context): PrinterService {
        return PrinterServiceImin(context)
    }

    companion object {
        @JvmStatic
        fun register() {
            PrinterRegistry.registerFactory(IminPrinterFactory())
        }
    }
}
