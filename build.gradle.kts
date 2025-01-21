val ktorVersion = "3.0.2"

plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "3.0.2"
}

group = "ir.mahdiparastesh"
version = "1.0.0"

application {
    mainClass.set("ir.mahdiparastesh.instatools.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")
}
