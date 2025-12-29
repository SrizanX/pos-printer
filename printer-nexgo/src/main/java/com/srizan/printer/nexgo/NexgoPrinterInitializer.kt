package com.srizan.printer.nexgo

import android.content.Context
import androidx.startup.Initializer

class NexgoPrinterInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        NexgoPrinterFactory.register()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
