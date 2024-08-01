package com.srizan.printer.enums

/**
 *
 * Printing position
 *
 * 0 — Not printed (Default)
 * 1 — Above the bar code
 * 2 — Below the bar code
 * 3 — Both above and below the bar code
 * */
enum class BarcodeTextPosition {
    HIDDEN,
    TOP,
    BOTTOM,
    TOP_AND_BOTTOM
}