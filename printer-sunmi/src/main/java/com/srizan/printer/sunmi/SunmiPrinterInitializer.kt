package com.srizan.printer.sunmi

import android.content.Context
import androidx.startup.Initializer

class SunmiPrinterInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        SunmiPrinterFactory.register()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
