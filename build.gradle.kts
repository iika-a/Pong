import org.gradle.api.file.DuplicatesStrategy

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    application // if you want to specify a main class for running
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    // FlatLaf
    implementation("com.formdev:flatlaf:3.5.4")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
}

// Optional: Set the main class for running the app via Gradle
application {
    mainClass.set("pink.iika.pong.MainKt") // Replace with your actual main class
}

tasks.register<Jar>("fatJar") {
    group = "build"
    archiveClassifier.set("all")

    manifest {
        attributes["Main-Class"] = "pink.iika.pong.MainKt"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    // FIX: avoid crashing on duplicate files in dependencies
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}

