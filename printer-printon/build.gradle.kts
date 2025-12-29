plugins {
    id("printer-vendor-convention")
}

android {
    namespace = "com.srizan.printer.printon"
}

dependencies {
    implementation(files("libs/printer-printon.aar"))
}