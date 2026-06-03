
import com.android.build.api.dsl.LibraryExtension
import gradle.kotlin.dsl.accessors._7c14c76da6582a6e8f7ea9ce2fab1dea.kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
}

configure<LibraryExtension> {
    compileSdk = 37

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    "implementation"(project(":printer-core"))
    "implementation"("androidx.startup:startup-runtime:1.2.0")
    
    "testImplementation"("junit:junit:4.13.2")
    "androidTestImplementation"("androidx.test.ext:junit:1.2.1")
    "androidTestImplementation"("androidx.test.espresso:espresso-core:3.6.1")
}
