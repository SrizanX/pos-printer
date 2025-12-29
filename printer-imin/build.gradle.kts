plugins {
    id("printer-vendor-convention")
}

android {
    namespace = "com.srizan.printer.imin"
}

dependencies {
    implementation(libs.printer.imin)
}