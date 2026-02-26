plugins {
    kotlin("jvm") version "2.2.10"
    application
}

application {
    mainClass.set("io.github.scontreraslopez.trainsim.MainKt")
}

group = "io.github.scontreraslopez"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}