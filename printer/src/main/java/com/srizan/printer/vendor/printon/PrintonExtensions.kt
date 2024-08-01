package com.srizan.printer.vendor.printon

import com.sr.SrPrinter

fun SrPrinter.setTextItalic(isItalic: Boolean) {
    printEpson(byteArrayOf(27, 45, if (isItalic) 1 else 0))
}

fun SrPrinter.setTextUnderlined(isUnderlined: Boolean) {
    printEpson(byteArrayOf(27, 45, if (isUnderlined) 1 else 0))
}

fun SrPrinter.setTextInverseColor(isInverseColor: Boolean) {
    printEpson(byteArrayOf(29, 66, if (isInverseColor) 1 else 0))
}