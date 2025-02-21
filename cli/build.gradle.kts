plugins { kotlin("jvm") }
kotlin { jvmToolchain(23) }

group = "ir.mahdiparastesh"
version = "3.1.0"

dependencies {
    implementation(project(":core"))
}

tasks.jar {
    archiveBaseName = "InstaTools"
    manifest {
        attributes["Main-Class"] = "ir.mahdiparastesh.instatools.MainKt"
        attributes["Manifest-Version"] = version
    }
    from(configurations.runtimeClasspath.get().map(::zipTree))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
