plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":spec"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}