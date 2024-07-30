package com.srizan.printer.nexgo

import com.nexgo.oaf.apiv3.device.printer.AlignEnum
import com.nexgo.oaf.apiv3.device.printer.BarcodeFormatEnum
import com.srizan.printer.Alignment
import com.srizan.printer.BarcodeSymbology

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

fun Alignment.getNexgoAlignEnum() = when (this) {
    Alignment.LEFT -> AlignEnum.LEFT
    Alignment.CENTER -> AlignEnum.CENTER
    Alignment.RIGHT -> AlignEnum.RIGHT
}