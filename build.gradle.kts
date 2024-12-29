plugins {
    kotlin("jvm") version "2.0.21"
}

group = "ir.mahdiparastesh"
version = "0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
