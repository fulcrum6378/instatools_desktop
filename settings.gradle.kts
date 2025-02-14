dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version("2.1.0") apply(false)
}

include(":core")
include(":cli")

rootProject.name = "InstaTools"
