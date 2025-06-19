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
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Finance Tracker"
include(":app")
include(":core")
include(":core:common")
include(":core:ui")
include(":core:navigation")
include(":core:testing")
include(":data")
include(":domain")
include(":feature")
include(":feature:transactions")
include(":feature:currency-conversion")
