package com.srizan.printer.sunmi

import android.content.Context
import com.srizan.printer.core.PrinterService
import com.srizan.printer.core.PrinterFactory
import com.srizan.printer.core.PrinterRegistry
import com.srizan.printer.core.enums.PrinterDevice

class SunmiPrinterFactory : PrinterFactory {
    override fun getSupportedDevice() = PrinterDevice.SUNMI
    
    override fun createPrinter(context: Context): PrinterService {
        return PrinterServiceSunmi(context)
    }
    
    companion object {
        @JvmStatic
        fun register() {
            PrinterRegistry.registerFactory(SunmiPrinterFactory())
        }
    }
}