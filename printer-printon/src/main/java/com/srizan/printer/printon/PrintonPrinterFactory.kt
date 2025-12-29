package com.srizan.printer.printon

import android.content.Context
import com.srizan.printer.core.AbstractPrinter
import com.srizan.printer.core.PrinterFactory
import com.srizan.printer.core.PrinterRegistry
import com.srizan.printer.core.enums.PrinterDevice

class PrintonPrinterFactory : PrinterFactory {
    override fun getSupportedDevice() = PrinterDevice.PRINTON
    
    override fun createPrinter(context: Context): AbstractPrinter {
        return PrinterPrinton(context)
    }
    
    companion object {
        init {
            PrinterRegistry.registerFactory(PrintonPrinterFactory())
        }
    }
}
