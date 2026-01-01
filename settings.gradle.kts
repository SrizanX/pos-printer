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
