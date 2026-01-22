package com.srizan.posprinter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.srizan.posprinter.databinding.ActivityMainBinding;
import com.srizan.printer.Printer;
import com.srizan.printer.core.config.BarcodeConfig;
import com.srizan.printer.core.config.QRCodeConfig;
import com.srizan.printer.core.config.TableConfig;
import com.srizan.printer.core.config.TextConfig;
import com.srizan.printer.core.enums.BarcodeSymbology;
import com.srizan.printer.core.enums.BarcodeTextPosition;
import com.srizan.printer.core.enums.PrinterAlignment;
import com.srizan.printer.core.enums.PrinterDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Example Activity demonstrating how to use the Printer library from Java code.
 * This is a complete Java equivalent of MainActivity.kt to prove full Java compatibility.
 */
public class MainActivityJava extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Initialize view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.topMargin = insets.top;
            mlp.rightMargin = insets.right;
            mlp.bottomMargin = insets.bottom;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        setContentView(binding.getRoot());

        // Set the action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Java Example");
            getSupportActionBar().setSubtitle("Printer: " + Printer.INSTANCE.getSelectedPrinter().name());
        }

        setupViews();
    }

    private void setupViews() {
        // Setup text printing section
        setTextFontSize(binding.layoutText.sliderTextSize.getValue());
        binding.layoutText.sliderTextSize.addOnChangeListener((slider, value, fromUser) ->
                setTextFontSize(value)
        );
        binding.layoutText.btnPrintText.setOnClickListener(v -> printText());

        // Setup QR code section
        setQRCodeSize(binding.layoutQr.sliderQrSize.getValue());
        binding.layoutQr.sliderQrSize.addOnChangeListener((slider, value, fromUser) ->
                setQRCodeSize(value)
        );
        binding.layoutQr.btnPrintQr.setOnClickListener(v -> printQRCode());

        // Setup barcode section
        setBarCodeHeight(binding.layoutBar.sliderBarHeight.getValue());
        setBarCodeWidth(binding.layoutBar.sliderBarWidth.getValue());

        // Setup barcode symbology spinner
        ArrayAdapter<BarcodeSymbology> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                BarcodeSymbology.values()
        );
        binding.layoutBar.spinnerSymbology.setAdapter(adapter);
        binding.layoutBar.spinnerSymbology.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BarcodeSymbology symbology = (BarcodeSymbology) parent.getItemAtPosition(position);
                setupBarcodeEditText(symbology);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.layoutBar.sliderBarHeight.addOnChangeListener((slider, value, fromUser) ->
                setBarCodeHeight(value)
        );
        binding.layoutBar.sliderBarWidth.addOnChangeListener((slider, value, fromUser) ->
                setBarCodeWidth(value)
        );
        binding.layoutBar.btnPrintBar.setOnClickListener(v -> printBarcode());
    }

    private void setTextFontSize(float size) {
        binding.layoutText.tvFontSize.setText(getString(R.string.font_size, (int) size));
    }

    private void setQRCodeSize(float size) {
        binding.layoutQr.tvQrSize.setText(getString(R.string.qr_size, (int) size));
    }

    private void setBarCodeHeight(float size) {
        binding.layoutBar.tvBarHeight.setText(getString(R.string.bar_height, (int) size));
    }

    private void setBarCodeWidth(float size) {
        binding.layoutBar.tvBarWidth.setText(getString(R.string.bar_width, (int) size));
    }

    /**
     * Print text with formatting based on UI inputs
     */
    private void printText() {
        String text = binding.layoutText.text.getText().toString().trim();

        // Determine alignment from radio buttons
        PrinterAlignment alignment;
        if (binding.layoutText.layoutAlignment.rbLeft.isChecked()) {
            alignment = PrinterAlignment.LEFT;
        } else if (binding.layoutText.layoutAlignment.rbCenter.isChecked()) {
            alignment = PrinterAlignment.CENTER;
        } else {
            alignment = PrinterAlignment.RIGHT;
        }

        // Create text configuration from UI inputs
        TextConfig config = new TextConfig(
                (int) binding.layoutText.sliderTextSize.getValue(),
                alignment,
                binding.layoutText.checkboxBold.isChecked(),
                binding.layoutText.checkboxItalic.isChecked(),
                binding.layoutText.checkboxUnderline.isChecked(),
                binding.layoutText.checkboxStrikethrough.isChecked(),
                binding.layoutText.checkboxInverseColor.isChecked()
        );

        //APPROACH 1: Using Printer.ifPrinterOperational() method (Kotlin-style)
        Printer.INSTANCE.ifPrinterOperational(() -> {
                    Printer.INSTANCE.printText(text, config);
                    Printer.INSTANCE.printNewLine(
                            binding.layoutText.checkboxAdd3LineSpace.isChecked() ? 3 : 0
                    );
                    return null;
                }
        );
    }

    /**
     * Print QR code based on UI inputs
     */
    private void printQRCode() {
        String qrContent = binding.layoutQr.textQr.getText().toString();

        // Determine alignment from radio buttons
        PrinterAlignment alignment;
        if (binding.layoutQr.layoutAlignment.rbLeft.isChecked()) {
            alignment = PrinterAlignment.LEFT;
        } else if (binding.layoutQr.layoutAlignment.rbCenter.isChecked()) {
            alignment = PrinterAlignment.CENTER;
        } else {
            alignment = PrinterAlignment.RIGHT;
        }

        Printer.INSTANCE.ifPrinterOperational(() -> {
                    QRCodeConfig config = new QRCodeConfig(
                            (int) binding.layoutQr.sliderQrSize.getValue(),
                            alignment
                    );
                    Printer.INSTANCE.printQRCode(qrContent, config);
                    return null;
                }
        );
    }

    /**
     * Print barcode based on UI inputs
     */
    private void printBarcode() {
        String barcodeContent = binding.layoutBar.textBarcode.getText().toString();

        // Determine alignment from radio buttons
        PrinterAlignment alignment;
        if (binding.layoutBar.layoutAlignment.rbLeft.isChecked()) {
            alignment = PrinterAlignment.LEFT;
        } else if (binding.layoutBar.layoutAlignment.rbCenter.isChecked()) {
            alignment = PrinterAlignment.CENTER;
        } else {
            alignment = PrinterAlignment.RIGHT;
        }

        // Determine text position from radio buttons
        BarcodeTextPosition textPosition;
        if (binding.layoutBar.rbBarTextPositionHidden.isChecked()) {
            textPosition = BarcodeTextPosition.HIDDEN;
        } else if (binding.layoutBar.rbBarTextPositionTop.isChecked()) {
            textPosition = BarcodeTextPosition.TOP;
        } else if (binding.layoutBar.rbBarTextPositionBottom.isChecked()) {
            textPosition = BarcodeTextPosition.BOTTOM;
        } else {
            textPosition = BarcodeTextPosition.TOP_AND_BOTTOM;
        }

        // Get selected barcode symbology
        BarcodeSymbology symbology = (BarcodeSymbology) binding.layoutBar.spinnerSymbology.getSelectedItem();

        Printer.INSTANCE.ifPrinterOperational(
                () -> {
                    // Create barcode configuration from UI inputs
                    BarcodeConfig config = new BarcodeConfig(
                            symbology,
                            (int) binding.layoutBar.sliderBarHeight.getValue(),
                            (int) binding.layoutBar.sliderBarWidth.getValue(),
                            alignment,
                            textPosition
                    );
                    Printer.INSTANCE.printBarcode(barcodeContent, config);
                    Printer.INSTANCE.printNewLine(3);
                    return null;
                }
        );
    }

    /**
     * Print a comprehensive sample demonstrating all features
     */
    private void printSample() {
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_jatri_bg_white);
        if (Printer.INSTANCE.isOperational()) {
            testPrint(logo);
        }
    }

    /**
     * Comprehensive test print demonstrating all library features
     */
    private void testPrint(Bitmap logo) {
        TextConfig defaultTextConfig = new TextConfig();
        TextConfig labelTextConfig = new TextConfig(
                26,
                PrinterAlignment.CENTER,
                true
        );

        // Bengali text
        Printer.INSTANCE.printText("Bengali\n", labelTextConfig);
        Printer.INSTANCE.printText("যাত্রী সার্ভিসেস লিমিটেড\n", defaultTextConfig);

        // Alignment examples
        Printer.INSTANCE.printText("\nAlignment\n", labelTextConfig);
        Printer.INSTANCE.printText(
                "যাত্রী সার্ভিসেস লিমিটেড\n",
                new TextConfig(24, PrinterAlignment.LEFT)
        );
        Printer.INSTANCE.printText(
                "যাত্রী সার্ভিসেস লিমিটেড\n",
                new TextConfig(24, PrinterAlignment.CENTER)
        );
        Printer.INSTANCE.printText(
                "যাত্রী সার্ভিসেস লিমিটেড\n",
                new TextConfig(24, PrinterAlignment.RIGHT)
        );

        // Font sizes
        Printer.INSTANCE.printText("\nFont Sizes\n", labelTextConfig);
        for (int n = 24; n <= 40; n++) {
            Printer.INSTANCE.printText(
                    "যাত্রী সার্ভিসেস লিমিটেড - " + n + "\n",
                    new TextConfig(n)
            );
        }

        // Table with 2 columns
        Printer.INSTANCE.printText("\nTable Print: 2 Columns\n", labelTextConfig);
        TableConfig tableConfig2Col = new TableConfig(
                new int[]{1, 1},
                new int[]{0, 2},
                new int[]{24, 24}
        );
        Printer.INSTANCE.printTable(new String[]{"Item", "Price"}, tableConfig2Col, defaultTextConfig);
        Printer.INSTANCE.printTable(new String[]{"ক", "৳১০০"}, tableConfig2Col, defaultTextConfig);
        Printer.INSTANCE.printTable(new String[]{"খ", "৳২০০"}, tableConfig2Col, defaultTextConfig);
        Printer.INSTANCE.printTable(new String[]{"গ", "৳৩০০"}, tableConfig2Col, defaultTextConfig);
        Printer.INSTANCE.printTable(new String[]{"ঘ", "৳৪০০"}, tableConfig2Col, defaultTextConfig);
        Printer.INSTANCE.printTable(new String[]{"ঙ", "৳৫০০"}, tableConfig2Col, defaultTextConfig);

        // Table with 3 columns
        Printer.INSTANCE.printText("\nTable Print: 3 Columns\n", labelTextConfig);
        TableConfig tableConfig3Col = new TableConfig(
                new int[]{1, 1, 1},
                new int[]{0, 1, 2},
                new int[]{24, 24, 24}
        );
        Printer.INSTANCE.printTable(new String[]{"Item", "Qty", "Price"}, tableConfig3Col, defaultTextConfig);
        Printer.INSTANCE.printTable(new String[]{"A", "1", "$10"}, tableConfig3Col, defaultTextConfig);
        Printer.INSTANCE.printTable(new String[]{"B", "2", "$20"}, tableConfig3Col, defaultTextConfig);
        Printer.INSTANCE.printTable(new String[]{"C", "3", "$30"}, tableConfig3Col, defaultTextConfig);
        Printer.INSTANCE.printNewLine(1);

        // Image printing
        if (logo != null) {
            Printer.INSTANCE.printText("\nImage\n", labelTextConfig);
            Printer.INSTANCE.printImage(logo, PrinterAlignment.CENTER);
            Printer.INSTANCE.printNewLine(1);
        }

        // QR Code
        Printer.INSTANCE.printText("\nQR Code\n", labelTextConfig);
        Printer.INSTANCE.printQRCode("Jatri Services Ltd.", new QRCodeConfig());
        Printer.INSTANCE.printNewLine(3);

        // Barcode
        Printer.INSTANCE.printText("\nBarcode\n", labelTextConfig);
        Printer.INSTANCE.printBarcode(
                "123456789012",
                new BarcodeConfig(BarcodeSymbology.CODE_128)
        );
        Printer.INSTANCE.printNewLine(3);
    }

    /**
     * Setup barcode input field based on selected symbology
     */
    private void setupBarcodeEditText(BarcodeSymbology symbology) {
        switch (symbology) {
            case UPC_A:
                setupTextEditText(12, InputType.TYPE_CLASS_NUMBER);
                break;
            case UPC_E:
                setupTextEditText(7, InputType.TYPE_CLASS_NUMBER);
                break;
            case EAN_13:
                setupTextEditText(12, InputType.TYPE_CLASS_NUMBER);
                break;
            case EAN_8:
                setupTextEditText(7, InputType.TYPE_CLASS_NUMBER);
                break;
            case CODE_39:
                setupTextEditText(-1, InputType.TYPE_CLASS_TEXT);
                break;
            case ITF:
            case CODABAR:
            case CODE_93:
                setupTextEditText(-1, InputType.TYPE_CLASS_NUMBER);
                break;
            case CODE_128:
                setupTextEditText(-1, InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    /**
     * Configure the barcode input field
     */
    private void setupTextEditText(int inputLength, int inputType) {
        binding.layoutBar.textBarcode.setInputType(inputType);
        binding.layoutBar.tilBarcode.setCounterMaxLength(inputLength);

        if (inputLength <= 0) {
            binding.layoutBar.textBarcode.setFilters(new InputFilter[]{});
        } else {
            binding.layoutBar.textBarcode.setFilters(
                    new InputFilter[]{new InputFilter.LengthFilter(inputLength)}
            );
        }
    }

    /**
     * Show dialog to select printer device
     */
    private void showPrinterSelectorDialog() {
        Set<PrinterDevice> availablePrintersSet = Printer.INSTANCE.getAvailablePrinters();
        List<PrinterDevice> availablePrinters = new ArrayList<>(availablePrintersSet);

        if (availablePrinters.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("No Printers Available")
                    .setMessage("No printer modules are included in the app. Please add vendor dependencies (e.g., implementation(projects.printerSunmi))")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Convert printer names to array
        String[] printerNames = new String[availablePrinters.size()];
        for (int i = 0; i < availablePrinters.size(); i++) {
            printerNames[i] = availablePrinters.get(i).name();
        }

        // Get currently selected printer index
        int currentSelection = availablePrinters.indexOf(Printer.INSTANCE.getSelectedPrinter());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your device")
                .setNegativeButton("Close", (dialog, which) -> {
                })
                .setSingleChoiceItems(printerNames, currentSelection, (dialog, which) -> {
                    onPrinterSelected(availablePrinters.get(which));
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Handle printer selection
     */
    private void onPrinterSelected(PrinterDevice printerDevice) {
        Printer.INSTANCE.selectPrinter(printerDevice);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle("Printer: " + Printer.INSTANCE.getSelectedPrinter().name());
        }
    }

    /**
     * Show a toast message
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_select_printer) {
            showPrinterSelectorDialog();
            return true;
        } else if (id == R.id.menu_print_sample) {
            printSample();
            return true;
        } else if (id == R.id.menu_get_printer_serial) {
            Printer.INSTANCE.ifPrinterOperational(
                    () -> {
                        String serialNumber = Printer.INSTANCE.getDeviceSerialNumber();
                        if (serialNumber != null) {
                            showToast(serialNumber);
                        }
                        return null;
                    }
            );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
