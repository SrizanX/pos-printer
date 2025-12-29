package com.srizan.printer.nexgo

import android.content.Context
import com.srizan.printer.core.PrinterService
import com.srizan.printer.core.PrinterFactory
import com.srizan.printer.core.PrinterRegistry
import com.srizan.printer.core.enums.PrinterDevice

class NexgoPrinterFactory : PrinterFactory {
    override fun getSupportedDevice() = PrinterDevice.NEXGO
    
    override fun createPrinter(context: Context): PrinterService {
        return PrinterServiceNexgo(context)
    }
    
    companion object {
        @JvmStatic
        fun register() {
            PrinterRegistry.registerFactory(NexgoPrinterFactory())
        }
    }
}
