package com.srizan.printer.vendor.usb

import com.srizan.printer.config.TextConfig
import com.srizan.printer.enums.PrinterAlignment


object UsbTextConfigurationBuilder {
    private val trailingNewlinesRegex = "(\\n*)$".toRegex()
    fun getConfiguredText(text: String, textConfiguration: TextConfig): String {
        val trailingNewlines: String = trailingNewlinesRegex.find(text)?.value ?: ""
        val textWithoutTrailingNewlines: String = text.removeSuffix(trailingNewlines)

        // Apply formatting in a clear, sequential manner
        var formattedText = applyFontStyle(textWithoutTrailingNewlines, textConfiguration)

        if (textConfiguration.isBold)
            formattedText = "<b>$formattedText</b>"

        if (textConfiguration.isUnderLined)
            formattedText = "<u>$formattedText</u>"

        formattedText = when (textConfiguration.printerAlignment) {
            PrinterAlignment.LEFT -> "[L]${formattedText}"
            PrinterAlignment.CENTER -> "[C]$formattedText"
            PrinterAlignment.RIGHT -> "[R]${formattedText}"
        }
        return formattedText + trailingNewlines
    }

    private fun applyFontStyle(input: String, textConfiguration: TextConfig): String {
        val fontSize = when (textConfiguration.size) {
            1 -> "normal"
            4 -> "big"
            2 -> "tall"
            3 -> "wide"
            else -> "normal"
        }
        val color = if (textConfiguration.isInverseColor) "bg-black" else "black"
        return "<font size='$fontSize' color='$color'>$input</font>"
    }
}