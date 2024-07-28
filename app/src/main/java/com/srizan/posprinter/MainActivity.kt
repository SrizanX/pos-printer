package com.srizan.posprinter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmapOrNull
import com.srizan.posprinter.databinding.ActivityMainBinding
import com.srizan.printer.Alignment
import com.srizan.printer.Printer
import com.srizan.printer.PrinterDevice
import com.srizan.printer.TextConfig

private const val TAG = "asd"
const val printer_key = "printer"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        prefs = getSharedPreferences("printer_pref", Context.MODE_PRIVATE)
        setContentView(binding.root)

        val device = prefs.getString(printer_key, PrinterDevice.SUNMI.name)
        device?.let { Printer.selectPrinter(PrinterDevice.valueOf(it)) }
        supportActionBar?.subtitle = Printer.selectedPrinter.name

        binding.layoutText.run {
            setTextFontSize(sliderTextSize.value)
            sliderTextSize.addOnChangeListener { _, value, _ -> setTextFontSize(value) }
            btnPrintText.setOnClickListener { printText() }
        }
        binding.layoutQr.run {
            setQRCodeSize(sliderQrSize.value)
            sliderQrSize.addOnChangeListener { _, value, _ -> setQRCodeSize(value) }
            btnPrintQr.setOnClickListener { printQRCode() }
        }
        binding.layoutBar.run {
            setBarCodeHeight(sliderBarHeight.value)
            setBarCodeWidth(sliderBarWidth.value)
            sliderBarHeight.addOnChangeListener { _, value, _ -> setBarCodeHeight(value) }
            sliderBarWidth.addOnChangeListener { _, value, _ -> setBarCodeWidth(value) }
            btnPrintBar.setOnClickListener { printBarCode() }
        }
    }

    private fun setTextFontSize(size: Float) {
        binding.layoutText.tvFontSize.text = getString(R.string.font_size, size.toInt())
    }

    private fun setQRCodeSize(size: Float) {
        binding.layoutQr.tvQrSize.text = getString(R.string.qr_size, size.toInt())
    }

    private fun setBarCodeHeight(size: Float) {
        binding.layoutBar.tvBarHeight.text = getString(R.string.bar_height, size.toInt())
    }

    private fun setBarCodeWidth(size: Float) {
        binding.layoutBar.tvBarWidth.text = getString(R.string.bar_width, size.toInt())
    }

    private fun printText() {
        binding.layoutText.apply {
            btnPrintText.setOnClickListener {
                val text = text.text.toString().trim()
                val alignment: Alignment = if (layoutAlignment.rbLeft.isChecked) Alignment.LEFT
                else if (layoutAlignment.rbCenter.isChecked) Alignment.CENTER else Alignment.RIGHT
                val textConfig = TextConfig(
                    size = sliderTextSize.value.toInt(),
                    alignment = alignment,
                    isBold = checkboxBold.isChecked,
                    isItalic = checkboxItalic.isChecked,
                    isUnderLined = checkboxUnderline.isChecked,
                    isStrikethrough = checkboxStrikethrough.isChecked,
                    isInverseColor = checkboxInverseColor.isChecked
                )
                Printer.printText(text, textConfig)
                Printer.printNewLine(if (checkboxAdd3LineSpace.isChecked) 3 else 0)
            }
        }
    }

    private fun printQRCode() {
        binding.layoutQr.run {
            val alignment: Alignment = if (layoutAlignment.rbLeft.isChecked) Alignment.LEFT
            else if (layoutAlignment.rbCenter.isChecked) Alignment.CENTER else Alignment.RIGHT
            Printer.printQRCode(
                data = textQr.text.toString(),
                size = sliderQrSize.value.toInt(),
                alignment = alignment
            )
        }
    }

    private fun printBarCode() {
        binding.layoutBar.run {
            val alignment: Alignment = if (layoutAlignment.rbLeft.isChecked) Alignment.LEFT
            else if (layoutAlignment.rbCenter.isChecked) Alignment.CENTER else Alignment.RIGHT
        }
    }

    private fun printSample() {
        val drawable = AppCompatResources.getDrawable(this, R.drawable.logo_jatri)
        val logo = drawable?.toBitmapOrNull()
        if (Printer.isOperational()) Printer.test(logo)
    }

    private fun showPrinterSelectorDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select your device").setNegativeButton("Close") { _, _ -> }
            .setSingleChoiceItems(
                PrinterDevice.entries.map { it.name }.toTypedArray(),
                PrinterDevice.entries.indexOf(Printer.selectedPrinter)
            ) { _, index ->
                onPrinterSelected(PrinterDevice.entries[index])

            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun onPrinterSelected(printerDevice: PrinterDevice) {
        Printer.selectPrinter(printerDevice)
        supportActionBar?.subtitle = Printer.selectedPrinter.name
        prefs.edit().putString(printer_key, printerDevice.name).apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_select_printer -> {
                showPrinterSelectorDialog()
                true
            }

            R.id.menu_print_sample -> {
                printSample()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

