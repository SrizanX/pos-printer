package com.srizan.printer.printon

import android.content.Context
import com.srizan.printer.core.PrinterService
import com.srizan.printer.core.PrinterFactory
import com.srizan.printer.core.PrinterRegistry
import com.srizan.printer.core.enums.PrinterDevice

class PrintonPrinterFactory : PrinterFactory {
    override fun getSupportedDevice() = PrinterDevice.PRINTON
    
    override fun createPrinter(context: Context): PrinterService {
        return PrinterServicePrinton(context)
    }
    
    companion object {
        @JvmStatic
        fun register() {
            PrinterRegistry.registerFactory(PrintonPrinterFactory())
        }
    }
}
