pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            isAllowInsecureProtocol = true
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://maven.pkg.github.com/SrizanX/pos-printer")
            credentials {
                username = "SrizanX"
                password =
                    "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"
            }
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


rootProject.name = "pos-printer"
include(":app")
include(":printer")
include(":printer-core")
include(":printer-sunmi")
include(":printer-imin")
include(":printer-nexgo")
include(":printer-printon")
include(":printer-nexgo-lib")
include(":printer-printon-lib")
include(":util-ditherer")
include(":util-canvas")
