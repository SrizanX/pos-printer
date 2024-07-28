package com.srizan.printer

enum class BarcodeSymbology {

    /**Data capacity of 12 numeric digits - 11 user specified and 1 check digit*/
    UPC_A,

    /**Data Capacity of 7 numeric digits - 6 user specified and 1 check digit)*/
    UPC_E,


    JAN13EAN13,
    JAN8EAN8,

    /**
     * A through Z), numeric digits (0 through 9) and a number of special characters (-, ., $, /, +, %, and space)
     * */
    CODE_39,
    ITF,
    CODABAR,
    CODE_93,

    /**Variable length alphanumeric data
     * Supports full 128-character ASCII character set
     * */
    CODE128,

}