package com.srizan.posprinter

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.srizan.printer.Printer

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

class PrinterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Printer.initializePrinter(applicationContext)
    }
}