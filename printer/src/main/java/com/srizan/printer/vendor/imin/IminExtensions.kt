package com.srizan.printer.vendor.imin

import com.srizan.printer.config.TextConfig

fun TextConfig.getTextStyleImin(): Int {
    return when {
        isBold && isItalic -> 3
        isBold -> 1
        isItalic -> 2
        else -> 0
    }
}