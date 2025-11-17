plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":spec"))
    runtimeOnly(project(":prvaImpl"))
    runtimeOnly(project(":drugaImpl"))
    runtimeOnly(project(":trecaImpl"))

    implementation(project(":calculations"))

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.ajalt.clikt:clikt:3.5.0")
    implementation("com.opencsv:opencsv:5.9")
    implementation("org.postgresql:postgresql:42.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}


application {
    mainClass.set("testApp.TestKt")
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    mergeServiceFiles() // include meta-inf services files
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}