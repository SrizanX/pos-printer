package com.srizan.printer.core.config

@Suppress("ArrayInDataClass")
data class TableConfig(
    val weightArray: IntArray,
    val alignmentArray: IntArray,
    val sizeArray: IntArray = intArrayOf()
)