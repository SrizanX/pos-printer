package com.srizan.posprinter

import android.graphics.Bitmap
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmapOrNull
import com.srizan.posprinter.databinding.ActivityMainBinding
import com.srizan.printer.Printer
import com.srizan.printer.core.config.BarcodeConfig
import com.srizan.printer.core.config.QRCodeConfig
import com.srizan.printer.core.config.TableConfig
import com.srizan.printer.core.config.TextConfig
import com.srizan.printer.core.enums.PrinterAlignment
import com.srizan.printer.core.enums.BarcodeSymbology
import com.srizan.printer.core.enums.BarcodeTextPosition
import com.srizan.printer.core.enums.PrinterDevice
import com.srizan.printer.ifPrinterOperational

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

            ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                BarcodeSymbology.entries.toTypedArray()
            ).also { arrayAdapter ->
                spinnerSymbology.adapter = arrayAdapter
                spinnerSymbology.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            setupBarcodeEditText(arrayAdapter.getItem(position) as BarcodeSymbology)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                    }
            }

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
            val text = text.text.toString().trim()
            val printerAlignment: PrinterAlignment =
                if (layoutAlignment.rbLeft.isChecked) PrinterAlignment.LEFT
                else if (layoutAlignment.rbCenter.isChecked) PrinterAlignment.CENTER else PrinterAlignment.RIGHT
            val textConfig = TextConfig(
                size = sliderTextSize.value.toInt(),
                printerAlignment = printerAlignment,
                isBold = checkboxBold.isChecked,
                isItalic = checkboxItalic.isChecked,
                isUnderLined = checkboxUnderline.isChecked,
                isStrikethrough = checkboxStrikethrough.isChecked,
                isInverseColor = checkboxInverseColor.isChecked
            )
            ifPrinterOperational {
                Printer.printText(text, textConfig)
                Printer.printNewLine(if (checkboxAdd3LineSpace.isChecked) 3 else 0)
            }
        }
    }

    private fun printQRCode() {
        binding.layoutQr.run {
            val printerAlignment: PrinterAlignment =
                if (layoutAlignment.rbLeft.isChecked) PrinterAlignment.LEFT
                else if (layoutAlignment.rbCenter.isChecked) PrinterAlignment.CENTER else PrinterAlignment.RIGHT

            ifPrinterOperational {
                Printer.printQRCode(
                    data = textQr.text.toString(),
                    QRCodeConfig(
                        size = sliderQrSize.value.toInt(),
                        printerAlignment = printerAlignment,
                    )
                )
            }
        }
    }

    private fun printBarCode() {
        binding.layoutBar.run {
            val printerAlignment: PrinterAlignment =
                if (layoutAlignment.rbLeft.isChecked) PrinterAlignment.LEFT
                else if (layoutAlignment.rbCenter.isChecked) PrinterAlignment.CENTER else PrinterAlignment.RIGHT

            val textPosition = if (rbBarTextPositionHidden.isChecked) BarcodeTextPosition.HIDDEN
            else if (rbBarTextPositionTop.isChecked) BarcodeTextPosition.TOP
            else if (rbBarTextPositionBottom.isChecked) BarcodeTextPosition.BOTTOM
            else BarcodeTextPosition.TOP_AND_BOTTOM

            val barcodeSymbology = spinnerSymbology.selectedItem as BarcodeSymbology

            val barcodeConfig = BarcodeConfig(
                height = sliderBarHeight.value.toInt(),
                width = sliderBarWidth.value.toInt(),
                printerAlignment = printerAlignment,
                symbology = barcodeSymbology,
                textPosition = textPosition
            )

            ifPrinterOperational {
                Printer.printBarcode(
                    data = textBarcode.text.toString(),
                    barcodeConfig
                )
                Printer.printNewLine(3)
            }
        }
    }

    private fun printSample() {
        val drawable = AppCompatResources.getDrawable(this, R.drawable.logo_jatri_bg_white)
        val logo = drawable?.toBitmapOrNull()
        if (Printer.isOperational()) testPrint(logo)
    }

    private fun testPrint(logo: Bitmap?) {
        val defaultTextConfig = TextConfig()
        val labelTextConfig =
            TextConfig(size = 26, printerAlignment = PrinterAlignment.CENTER, isBold = true)

        Printer.printText("Bengali\n", labelTextConfig)
        Printer.printText("যাত্রী সার্ভিসেস লিমিটেড\n", defaultTextConfig)

        Printer.printText("\nAlignment\n", labelTextConfig)
        Printer.printText(
            "যাত্রী সার্ভিসেস লিমিটেড\n",
            defaultTextConfig.copy(printerAlignment = PrinterAlignment.LEFT)
        )
        Printer.printText(
            "যাত্রী সার্ভিসেস লিমিটেড\n",
            defaultTextConfig.copy(printerAlignment = PrinterAlignment.CENTER)
        )
        Printer.printText(
            "যাত্রী সার্ভিসেস লিমিটেড\n",
            defaultTextConfig.copy(printerAlignment = PrinterAlignment.RIGHT)
        )

        Printer.printText("\nFont Sizes\n", labelTextConfig)
        (24..40 step 1).forEach { n ->
            Printer.printText("যাত্রী সার্ভিসেস লিমিটেড - $n\n", defaultTextConfig.copy(size = n))
        }

        Printer.printText("\nTable Print: 2 Columns\n", labelTextConfig)
        val tableConfig2Col = TableConfig(
            weightArray = intArrayOf(1, 1),
            alignmentArray = intArrayOf(0, 2),
            sizeArray = intArrayOf(24, 24)
        )
        Printer.printTable(arrayOf("Item", "Price"), tableConfig2Col, defaultTextConfig)
        Printer.printTable(arrayOf("ক", "৳১০০"), tableConfig2Col, defaultTextConfig)
        Printer.printTable(arrayOf("খ", "৳২০০"), tableConfig2Col, defaultTextConfig)
        Printer.printTable(arrayOf("গ", "৳৩০০"), tableConfig2Col, defaultTextConfig)
        Printer.printTable(arrayOf("ঘ", "৳৪০০"), tableConfig2Col, defaultTextConfig)
        Printer.printTable(arrayOf("ঙ", "৳৫০০"), tableConfig2Col, defaultTextConfig)

        Printer.printText("\nTable Print: 3 Columns\n", labelTextConfig)

        val tableConfig3Col = TableConfig(
            weightArray = intArrayOf(1, 1, 1),
            alignmentArray = intArrayOf(0, 1, 2),
            sizeArray = intArrayOf(24, 24, 24)
        )

        Printer.printTable(
            arrayOf("Item", "Qty", "Price"), tableConfig3Col, defaultTextConfig
        )
        Printer.printTable(
            arrayOf("A", "1", "$10"), tableConfig3Col, defaultTextConfig
        )
        Printer.printTable(
            arrayOf("B", "2", "$20"), tableConfig3Col, defaultTextConfig
        )
        Printer.printTable(
            arrayOf("C", "3", "$30"), tableConfig3Col, defaultTextConfig
        )
        Printer.printNewLine(1)

        logo?.let {
            Printer.printText("\nImage\n", labelTextConfig)
            Printer.printImage(logo, PrinterAlignment.CENTER)
            Printer.printNewLine(1)
        }

        Printer.printText("\nQR Code\n", labelTextConfig)
        Printer.printQRCode("Jatri Services Ltd.", QRCodeConfig())
        Printer.printNewLine(3)

        Printer.printText("\nBarcode\n", labelTextConfig)
        Printer.printBarcode("123456789012", BarcodeConfig(symbology = BarcodeSymbology.CODE_128))
        Printer.printNewLine(3)
    }

    private fun setupBarcodeEditText(symbology: BarcodeSymbology) {
        when (symbology) {
            BarcodeSymbology.UPC_A -> {
                setupTextEditText(
                    inputLength = 12,
                    inputType = InputType.TYPE_CLASS_NUMBER
                )
            }

            BarcodeSymbology.UPC_E -> {
                setupTextEditText(
                    inputLength = 7,
                    inputType = InputType.TYPE_CLASS_NUMBER
                )
            }

            BarcodeSymbology.EAN_13 -> {
                setupTextEditText(
                    inputLength = 12,
                    inputType = InputType.TYPE_CLASS_NUMBER
                )
            }

            BarcodeSymbology.EAN_8 -> {
                setupTextEditText(
                    inputLength = 7,
                    inputType = InputType.TYPE_CLASS_NUMBER
                )
            }

            BarcodeSymbology.CODE_39 -> {
                setupTextEditText(
                    inputType = InputType.TYPE_CLASS_TEXT
                )
            }

            BarcodeSymbology.ITF -> {
                setupTextEditText(
                    inputType = InputType.TYPE_CLASS_NUMBER
                )
            }

            BarcodeSymbology.CODABAR -> {
                setupTextEditText(
                    inputType = InputType.TYPE_CLASS_NUMBER
                )
            }

            BarcodeSymbology.CODE_93 -> {
                setupTextEditText(
                    inputType = InputType.TYPE_CLASS_NUMBER
                )
            }

            BarcodeSymbology.CODE_128 -> {
                setupTextEditText(
                    inputType = InputType.TYPE_CLASS_TEXT
                )
            }
        }
    }

    private fun setupTextEditText(
        inputLength: Int = -1,
        inputType: Int,
    ) {
        binding.layoutBar.apply {
            textBarcode.inputType = inputType
            tilBarcode.counterMaxLength = inputLength
            textBarcode.filters =
                if (inputLength <= 0) arrayOf() else arrayOf(InputFilter.LengthFilter(inputLength))

        }
    }

    private fun showPrinterSelectorDialog() {
        val availablePrinters = Printer.getAvailablePrinters().toList()

        if (availablePrinters.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("No Printers Available")
                .setMessage("No printer modules are included in the app. Please add vendor dependencies (e.g., implementation(projects.printerSunmi))")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select your device").setNegativeButton("Close") { _, _ -> }
            .setSingleChoiceItems(
                availablePrinters.map { it.name }.toTypedArray(),
                availablePrinters.indexOf(Printer.selectedPrinter)
            ) { _, index ->
                onPrinterSelected(availablePrinters[index])

            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun onPrinterSelected(printerDevice: PrinterDevice) {
        Printer.selectPrinter(printerDevice)
        supportActionBar?.subtitle = Printer.selectedPrinter.name

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

            R.id.menu_get_printer_serial -> {
                ifPrinterOperational {
                    Printer.getDeviceSerialNumber()?.let { showToast(it) }
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

