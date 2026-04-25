pluginManagement {
    includeBuild("build-logic")
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
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Lask"

include(":lask")

include(":core:network")
include(":core:database")
include(":core:ui")
include(":core:datastore")
include(":core:models")
include(":core:navigation")

include(":feature:news_feed:api")
include(":feature:news_feed:impl")

include(":feature:article_detail:api")
include(":feature:article_detail:impl")

include(":feature:bookmarks:api")
include(":feature:bookmarks:impl")

include(":feature:profile:api")
include(":feature:profile:impl")

include(":feature:explore:api")
include(":feature:explore:impl")

include(":feature:search:api")
include(":feature:search:impl")

include(":macrobenchmark")
include(":core:utils")
include(":core:platform")
