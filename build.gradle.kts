val ktorVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "3.0.2"
}

group = "ir.mahdiparastesh"
version = "0.3.0"

application {
    mainClass.set("ir.mahdiparastesh.instatools.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("org.apache.commons:commons-text:1.11.0")
}
