package com.srizan.printer.core

import com.srizan.printer.core.enums.PrinterDevice

object PrinterRegistry {
    private val factories = mutableMapOf<PrinterDevice, PrinterFactory>()

    fun registerFactory(factory: PrinterFactory) {
        factories[factory.getSupportedDevice()] = factory
    }

    fun getFactory(device: PrinterDevice): PrinterFactory? {
        return factories[device]
    }

    fun getAvailableDevices(): Set<PrinterDevice> {
        return factories.keys
    }
}