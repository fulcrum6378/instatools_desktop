val ktorVersion = "3.0.2"

plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "3.0.2"
}

group = "ir.mahdiparastesh"
version = "0.8.0"

application {
    mainClass.set("ir.mahdiparastesh.instatools.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")
    implementation("org.slf4j:slf4j-nop:2.0.16")  // suppress no SLF4J logger warnings
}
