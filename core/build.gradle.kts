plugins { kotlin("jvm") }
kotlin { jvmToolchain(23) }

sourceSets.getByName("main") {
    kotlin.srcDirs("src/kotlin")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")
    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.apache.httpcomponents:httpcore:4.4.16")
}
