package com.srizan.posprinter

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.srizan.printer.Printer
import com.srizan.printer.PrinterDevice

private const val TAG = "asd"
const val printer_key = "printer"
val Context.prefs: SharedPreferences
    get() = this.getSharedPreferences("printer_pref", Context.MODE_PRIVATE)

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

class PrinterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Printer.initializePrinter(applicationContext)
        val device = prefs.getString(printer_key, PrinterDevice.SUNMI.name)
        device?.let { Printer.selectPrinter(PrinterDevice.valueOf(it)) }
    }
}