plugins {
    id("printer-vendor-convention")
}

android {
    namespace = "com.srizan.printer.printon"
}

dependencies {
    //implementation(files("libs/printer-printon.aar"))
    implementation("com.srizan.printer:printon-lib:1.0.0")

}