package com.srizan.printer.core

import android.content.Context
import com.srizan.printer.core.enums.PrinterDevice

interface PrinterFactory {
    fun getSupportedDevice(): PrinterDevice
    fun createPrinter(context: Context): AbstractPrinter
}