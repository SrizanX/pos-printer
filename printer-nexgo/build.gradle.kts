plugins {
    id("printer-vendor-convention")
}

android {
    namespace = "com.srizan.printer.nexgo"
}

dependencies {
    implementation(files("libs/printer-nexgo.aar"))
}