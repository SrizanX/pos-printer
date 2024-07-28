package com.srizan.printer

data class TableConfig(
    val weightArray: IntArray,
    val alignmentArray: IntArray,
    val sizeArray: IntArray = intArrayOf()
)