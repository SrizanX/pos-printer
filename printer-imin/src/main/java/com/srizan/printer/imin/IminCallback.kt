package com.srizan.printer.imin

import android.util.Log
import com.imin.printer.INeoPrinterCallback

class IminCallback(val serial: (String?) -> Unit) : INeoPrinterCallback() {
    override fun onRunResult(isSuccess: Boolean) {
        Log.d("asd", "onRunResult -> isSuccess: $isSuccess")
    }

    override fun onReturnString(result: String?) {
        serial(result)
    }

    override fun onRaiseException(code: Int, msg: String?) {
        Log.d("asd", "onRaiseException -> code: $code msg: $msg")
    }

    override fun onPrintResult(code: Int, msg: String?) {
        Log.d("asd", "onPrintResult -> code: $code msg: $msg")

    }
}