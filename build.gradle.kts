group = "ir.mahdiparastesh"
version = "1.5.5"

plugins { kotlin("jvm") version "2.1.0" }

repositories { mavenCentral() }

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.commons:commons-imaging:1.0-alpha3")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.apache.httpcomponents:httpcore:4.4.16")
}

tasks.jar {
    manifest { attributes["Main-Class"] = "ir.mahdiparastesh.instatools.MainKt" }
    from(configurations.runtimeClasspath.get().map(::zipTree))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
