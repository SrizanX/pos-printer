# POS Printer Library for Android

A versatile Android library for thermal receipt printers supporting multiple printer vendors.

## Supported Printer Vendors

- Sunmi
- iMin
- Printon
- Nexgo

## Features

✅ **Full Java & Kotlin Support** - Works seamlessly with both languages

### Print your formatted texts

![Text](screenshots/01_text_formatting.png "Print your formatted texts")

### Print QR Code and Bar Code

![Codes](screenshots/02_codes.png "Print QR Code and Bar Code")

### Change your printer vendor

![Printer](screenshots/03_supported_printers.png "Change your printer vendor")

## Installation

> **Note:** This library is not yet published to Maven Central or JitPack.

### Option 1: Add as a module dependency

1. Clone this repository:

   ```bash
   git clone https://github.com/yourusername/pos-printer.git
   ```

2. Copy the `printer` module to your project directory

3. Add the module to your project's `settings.gradle.kts`:

   ```kotlin
   include(":app", ":printer")
   ```

4. Add the dependency to your app's `build.gradle.kts`:
   ```kotlin
   implementation(project(":printer"))
   ```

### Option 2: Manual AAR integration

1. Clone this repository and build the project to generate AAR file
2. Copy the generated AAR file from `printer/build/outputs/aar/` to your project's `libs` folder
3. Add the dependency to your app's `build.gradle.kts`:
   ```kotlin
   implementation(files("libs/printer-release.aar"))
   ```

## Usage

### Kotlin

Initialize the printer in your application:

```kotlin
Printer.initializePrinter(context)
```

Print text with formatting:

```kotlin
val textConfig = TextConfig(
    size = 24,
    printerAlignment = PrinterAlignment.CENTER,
    isBold = true
)
Printer.printText("Hello World!", textConfig)
```

Print QR Code:

```kotlin
Printer.printQRCode("https://example.com", QRCodeConfig())
```

Print Barcode:

```kotlin
Printer.printBarcode("1234567890", BarcodeConfig(symbology = BarcodeSymbology.CODE128))
```

### Java

The library is fully compatible with Java. Here are the equivalent examples:

Initialize the printer:

```java
Printer.INSTANCE.initializePrinter(context);
```

Print text with formatting:

```java
TextConfig textConfig = new TextConfig(
    24,                        // size
    PrinterAlignment.CENTER,   // alignment
    true,                      // isBold
    false,                     // isUnderline
    false,                     // isDoubleHeight
    false                      // isDoubleWidth
);
Printer.INSTANCE.printText("Hello World!", textConfig);
```

Print QR Code:

```java
QRCodeConfig qrConfig = new QRCodeConfig(
    200,                       // size
    PrinterAlignment.CENTER,   // alignment
    null                      // errorCorrectionLevel
);
Printer.INSTANCE.printQRCode("https://example.com", qrConfig);
```

Print Barcode:

```java
BarcodeConfig barcodeConfig = new BarcodeConfig(
    BarcodeSymbology.CODE128,  // symbology
    100,                       // height
    2,                         // width
    PrinterAlignment.CENTER,   // alignment
    null                      // textPosition
);
Printer.INSTANCE.printBarcode("1234567890", barcodeConfig);
```

> **Note:** When using the library from Java, access the singleton instance using `Printer.INSTANCE` instead of just `Printer`.

For a complete Java example, see [JavaExampleActivity.java](app/src/main/java/com/srizan/posprinter/JavaExampleActivity.java) in the sample app.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Note on third-party SDKs:** This library incorporates SDKs from various printer vendors (Sunmi, iMin, Printon, Nexgo) which may be subject to different license terms. Users of this library are responsible for complying with the respective license terms of these third-party components. See the [LICENSE](LICENSE) file for more information.
