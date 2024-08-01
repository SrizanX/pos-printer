package com.srizan.printer.enums

/**
 * Level L (Low)        7% of data bytes can be restored.
 * Level M (Medium)     15% of data bytes can be restored.
 * Level Q (Quartile)	25% of data bytes can be restored.
 * Level H (High)	    30% of data bytes can be restored.
 * */
enum class QRCodeErrorCorrectionLevel { L, M, Q, H }