plugins {
    kotlin("jvm") version "1.7.10"
    application
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    implementation("org.processing:core:3.3.7")
    implementation(project(":gui-core"))
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    archiveFileName.set("swingby-gui.jar")
    manifest {
        attributes["Main-Class"] = "de.arindy.swingby.gui.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
