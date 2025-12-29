package com.srizan.printer.printon

import android.content.Context
import androidx.startup.Initializer

class PrintonPrinterInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        PrintonPrinterFactory.register()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
