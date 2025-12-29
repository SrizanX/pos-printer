package com.srizan.printer.imin

import android.content.Context
import androidx.startup.Initializer

class IminPrinterInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        IminPrinterFactory.register()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
