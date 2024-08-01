package com.srizan.printer.config

@Suppress("ArrayInDataClass")
data class TableConfig(
    val weightArray: IntArray,
    val alignmentArray: IntArray,
    val sizeArray: IntArray = intArrayOf()
)