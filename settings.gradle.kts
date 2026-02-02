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
    }
}

rootProject.name = "F1 Sandbox"
include(":app")

// Core infrastructure modules
include(":core:common")
include(":core:common-test")
include(":core:database")
include(":core:ui")
include(":core:navigation")

// Race domain modules
include(":race:contracts")
include(":race:lib-models-race")
include(":race:lib-models-race-api")
include(":race:lib-models-race-fixtures")
include(":race:lib-api-race")
include(":race:lib-db-race")
include(":race:lib-store-race")
include(":race:lib-store-race-impl")
include(":race:feature-home")
include(":race:feature-results")

// Driver domain modules
include(":driver:contracts")
include(":driver:lib-models-driver")
include(":driver:lib-models-driver-api")
include(":driver:lib-models-driver-fixtures")
include(":driver:lib-api-driver")
include(":driver:lib-db-driver")
include(":driver:lib-store-driver")
include(":driver:lib-store-driver-impl")
include(":driver:feature-drivers")

// Settings domain modules
include(":settings:contracts")
include(":settings:feature-settings")
