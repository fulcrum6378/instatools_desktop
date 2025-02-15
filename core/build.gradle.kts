plugins { kotlin("jvm") }
kotlin { jvmToolchain(23) }

sourceSets.getByName("main") {
    kotlin.srcDirs("src/kotlin")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")
}
