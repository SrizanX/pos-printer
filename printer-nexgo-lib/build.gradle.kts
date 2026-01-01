plugins {
    id("maven-publish")
}

group = "com.srizan.printer"
version = "1.0.0"

publishing {
    publications {
        create<MavenPublication>("release") {
            artifactId = "nexgo-lib"
            artifact("libs/nexgo.aar")
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/SrizanX/pos-printer")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
