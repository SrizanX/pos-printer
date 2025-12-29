package com.srizan.printer.imin

import com.srizan.printer.core.config.TextConfig

fun TextConfig.getTextStyleImin(): Int {
    return when {
        isBold && isItalic -> 3
        isBold -> 1
        isItalic -> 2
        else -> 0
    }
}