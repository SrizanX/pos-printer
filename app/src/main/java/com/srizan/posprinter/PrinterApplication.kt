package com.srizan.posprinter

import android.app.Application
import com.srizan.printer.Printer

class PrinterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Printer.initializePrinter(applicationContext)
    }
}