plugins {
    id("printer-vendor-convention")
}

android {
    namespace = "com.srizan.printer.sunmi"
}

dependencies {
    implementation(libs.printer.sunmi)
}