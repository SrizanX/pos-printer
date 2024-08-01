package com.srizan.printer.vendor.nexgo

import com.nexgo.oaf.apiv3.device.printer.AlignEnum
import com.nexgo.oaf.apiv3.device.printer.BarcodeFormatEnum
import com.srizan.printer.enums.PrinterAlignment
import com.srizan.printer.enums.BarcodeSymbology

fun BarcodeSymbology.getNexgoSymbology() = when (this) {
    BarcodeSymbology.UPC_A -> BarcodeFormatEnum.UPC_A
    BarcodeSymbology.UPC_E -> BarcodeFormatEnum.UPC_E
    BarcodeSymbology.EAN_13 -> BarcodeFormatEnum.EAN_13
    BarcodeSymbology.EAN_8 -> BarcodeFormatEnum.EAN_8
    BarcodeSymbology.CODE_39 -> BarcodeFormatEnum.CODE_39
    BarcodeSymbology.ITF -> BarcodeFormatEnum.ITF
    BarcodeSymbology.CODABAR -> BarcodeFormatEnum.CODABAR
    BarcodeSymbology.CODE_93 -> BarcodeFormatEnum.CODE_93
    BarcodeSymbology.CODE_128 -> BarcodeFormatEnum.CODE_128
}

fun PrinterAlignment.getNexgoAlignEnum() = when (this) {
    PrinterAlignment.LEFT -> AlignEnum.LEFT
    PrinterAlignment.CENTER -> AlignEnum.CENTER
    PrinterAlignment.RIGHT -> AlignEnum.RIGHT
}